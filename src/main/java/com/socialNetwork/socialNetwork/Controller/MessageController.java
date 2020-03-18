/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Controller;

import com.socialNetwork.socialNetwork.DAO.Chat.ChatService;
import com.socialNetwork.socialNetwork.DAO.Message.MessageService;
import com.socialNetwork.socialNetwork.DAO.Profile.ProfileService;
import com.socialNetwork.socialNetwork.Entity.Chat;
import com.socialNetwork.socialNetwork.Entity.Message;
import com.socialNetwork.socialNetwork.Entity.Profile;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author user
 */
@Validated
@RestController
@CrossOrigin(origins = "http://localhost:8080",allowCredentials = "true",allowedHeaders = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PATCH,RequestMethod.DELETE})
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ProfileService profileService;
    
    @GetMapping("/message/{id}")
    public ResponseEntity<Message> getMessage(@PathVariable @Min(1) Long id){
        Message message=messageService.findById(id);
        if (message!=null)
        {
            Chat chat=message.getChat();
            chatService.initializeProfile(chat);
            Profile currentProfile=getAuthenticatedProfileFromDB();
            boolean found=false;
            for (Profile profile:chat.getProfiles())
            {
                if (profile.getId()==currentProfile.getId())
                    found=true;
            }
            if (found)
                return new ResponseEntity<Message>(message, HttpStatus.FOUND);
            else
                return new ResponseEntity<Message>(HttpStatus.FORBIDDEN);
        }
        else
            return new ResponseEntity<Message>(HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping(value = "/message",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> postMessage(@RequestBody Message message){
        Profile currentProfile=getAuthenticatedProfileFromDB();
        if ((message.getText()!=null && message.getDate()!=null && message.getChat()!=null && message.getSender()!=null) && (message.getSender()!=null && currentProfile.getId()==message.getSender().getId())) 
        {
          Chat chatFromDB=chatService.findById(message.getChat().getId());
          if (chatFromDB!=null)
          {
            message.setId(null);
            message.setSender(currentProfile);
            message.setChat(chatFromDB);
            chatFromDB.getMessages().add(message);
            currentProfile.getMessages().add(message);
            message=messageService.save(message);
          return new ResponseEntity<Message>(message, HttpStatus.CREATED);
          }
          else
            return new ResponseEntity<Message>(HttpStatus.BAD_REQUEST);
        }
        else 
            return new ResponseEntity<Message>(HttpStatus.BAD_REQUEST);
    }
    
    @DeleteMapping("/message")
    public ResponseEntity<Message> deleteMessage(@RequestBody Message message){
        if(message!=null && message.getId()!=null)
        {
            Message messageFromBD=messageService.findById(message.getId());
            if (messageFromBD!=null ){
                Profile currentProfile=getAuthenticatedProfileFromDB();
                if (currentProfile.getId()==messageFromBD.getSender().getId())
                {
                    messageFromBD.setChat(null);//!
                    messageFromBD.setSender(null);//!
                    messageService.delete(messageFromBD);
                    return new ResponseEntity<Message>(HttpStatus.FOUND);
                }
                else
                    return new ResponseEntity<Message>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Message>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Message>(HttpStatus.BAD_REQUEST);
    }
    
    @PatchMapping(value = "/message")
    public ResponseEntity<Message> patchMessage(@RequestBody @Valid Message message){
        Profile currentProfile=getAuthenticatedProfileFromDB();
        profileService.initializeMessage(currentProfile);
        if(message.getChat()!=null)
        {
            Message messageFromDB=messageService.findById(message.getId());
            if (message.getSender().getId()!=null && messageFromDB.getSender().getId()==currentProfile.getId() && message.getChat().getId()!=null)
            {
                Profile newSender=profileService.findById(message.getSender().getId());//
               Chat chatFromDB=chatService.findById(message.getChat().getId());
               if (chatFromDB!=null && newSender!=null && chatFromDB!=null)
               {
                  messageFromDB=updateMessageRelations(messageFromDB,chatFromDB,newSender,message.getText(),message.getDate());
                   return new ResponseEntity<Message>(messageFromDB,HttpStatus.ACCEPTED);
               }
               else
                   return new ResponseEntity<Message>(HttpStatus.BAD_REQUEST);
            }
            else
                return new ResponseEntity<Message>(HttpStatus.FORBIDDEN);
        }
        else
            return new ResponseEntity<Message>(HttpStatus.BAD_REQUEST);
    }
    
    private Profile getAuthenticatedProfileFromDB(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if (authentication!=null)
        {
            User userDetails=(User)(authentication.getPrincipal());
            if (userDetails!=null)
            {
                Profile profile=profileService.findByLogin(userDetails.getUsername());
                return profile;
            }
            else 
                return null;
        }
        else
            return null;
    }

    private Message updateMessageRelations(Message messageFromDB,Chat newChat,Profile newProfile,String text,Date date) {
        Profile oldProfile=null;
        Chat oldChat=null;
        messageFromDB.setDate(date);
        messageFromDB.setText(text);
        Long messageId=messageFromDB.getId();
        if (messageFromDB.getSender()!=null && messageFromDB.getSender().getId()!=null)
        {
            oldProfile=profileService.findById(messageFromDB.getSender().getId());
            if(oldProfile!=null && oldProfile.getId()!=newProfile.getId())
            {
                profileService.initializeMessage(newProfile);
                profileService.initializeMessage(oldProfile);
                oldProfile.getMessages().removeIf((Message message)->message.getId()==messageId);
                newProfile.getMessages().add(messageFromDB);
                messageFromDB.setSender(newProfile);
                messageFromDB=messageService.save(messageFromDB);
            }
        }
        if (messageFromDB.getChat()!=null && messageFromDB.getChat().getId()!=null)
        {
            oldChat=chatService.findById(messageFromDB.getChat().getId());
            if(oldChat!=null && oldChat.getId()!=newChat.getId())
            {
                chatService.initializeMessages(newChat);
                chatService.initializeMessages(oldChat);
                oldChat.getMessages().removeIf((Message message)->message.getId()==messageId);
                newChat.getMessages().add(messageFromDB);
                messageFromDB.setChat(newChat);
                messageFromDB=messageService.save(messageFromDB);
            }
        }
        return messageService.save(messageFromDB);
    }
        
}
