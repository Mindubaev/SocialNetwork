/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Controller;

import com.google.common.collect.Lists;
import com.socialNetwork.socialNetwork.DAO.Chat.ChatService;
import com.socialNetwork.socialNetwork.DAO.Message.MessageService;
import com.socialNetwork.socialNetwork.DAO.Profile.ProfileService;
import com.socialNetwork.socialNetwork.Entity.Chat;
import com.socialNetwork.socialNetwork.Entity.Message;
import com.socialNetwork.socialNetwork.Entity.Profile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Artur
 */
@RestController
@Validated
@CrossOrigin(origins = "http://localhost:8080",allowCredentials = "true",allowedHeaders = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PATCH,RequestMethod.DELETE})
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private AuthenticationManager myAuthenticationManager;
    
    @GetMapping(value="/chat/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Chat> getChat(@PathVariable @Min(1) Long id){
        Chat chat=chatService.findById(id);
        if (chat!=null && hasAcces(chat)){
            return new ResponseEntity<Chat>(chat, HttpStatus.FOUND);
        }
        else
            return new ResponseEntity<Chat>(HttpStatus.FORBIDDEN);
    }
    
    @GetMapping(value="/chat",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Chat>> getChat(){
        List<Chat> chats=Lists.newArrayList(getAuthenticatedProfileFromDB().getChats());
        if (chats!=null){
            return new ResponseEntity<List<Chat>>(chats, HttpStatus.FOUND);
        }
        else
            return new ResponseEntity<List<Chat>>(HttpStatus.FORBIDDEN);
    }

    private boolean hasAcces(Chat chat) {
        Set<Profile> profiles=chat.getProfiles();
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if (chat.getProfiles()!=null && authentication!=null)
        {
            User userDetails=(User)(authentication.getPrincipal());
            if (userDetails!=null)
            {
                Profile profile=profileService.findByLogin(userDetails.getUsername());
                for (Profile p:profiles)
                {
                    if (p.getId().equals(profile.getId()))
                        return true;
                }
            }
        }
        return false;
    }
    
   @PostMapping(value = "/chat",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Chat> postChat(@RequestBody Chat chat){
       if (chat!=null && chat.getTitle()!=null){
           Profile profile=getAuthenticatedProfileFromDB();
           Chat chatforSave=new Chat(chat.getTitle());
           chatforSave.getProfiles().add(profile);
           profileService.initializeChats(profile);
           profile.getChats().add(chatforSave);
           chatforSave.setId(null);
           return new ResponseEntity<Chat>(chatService.save(chatforSave), HttpStatus.CREATED);
       }
       return new ResponseEntity<Chat>(HttpStatus.BAD_REQUEST);
   }
   
   @PostMapping(value = "/chat/{id}/profiles")
   public ResponseEntity<Chat> postToChatProfiles(@RequestBody Profile profile,@PathVariable @Min(1) Long id){
       Chat chat=chatService.findById(id);
        if (chat!=null && profile!=null && profile.getId()!=null){
            chatService.initializeProfile(chat);
            Profile profileFromDB=profileService.findById(profile.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            if (profileFromDB!=null){
                profileService.initializeChats(profileFromDB);
                if (chat.getProfiles().size()==0 || (chat.contain(currentProfile) && !chat.contain(profileFromDB)))
                {
                    chat.getProfiles().add(profileFromDB);
                    profileFromDB.getChats().add(chat);
                    return new ResponseEntity<Chat>(chatService.save(chat),HttpStatus.CREATED);
                }
                else
                    return new ResponseEntity<Chat>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Chat>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Chat>(HttpStatus.BAD_REQUEST);
   }
   
   @DeleteMapping(value = "/chat/{id}/profiles")
   public ResponseEntity<Chat> deleteProfileFromChat(@RequestBody Profile profile,@PathVariable @Min(1) Long id){
       Chat chat=chatService.findById(id);
        if (chat!=null && profile!=null && profile.getId()!=null)
        {
            chatService.initializeProfile(chat);
            Profile profileFromDB=profileService.findById(profile.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            if (profileFromDB!=null){
                profileService.initializeChats(profileFromDB);
                if (chat.getProfiles().size()==0 || (profileFromDB.getId()==currentProfile.getId() && chat.contain(profileFromDB)))
                {
                    chat.getProfiles().removeIf(new Predicate<Profile>() {
                        @Override
                        public boolean test(Profile profile) {
                            if (profile.getId()==profileFromDB.getId())
                                return true;
                            else 
                                return false;
                        }
                    });
                    profileFromDB.getChats().removeIf(new Predicate<Chat>() {
                        @Override
                        public boolean test(Chat arg0) {
                            if (arg0.getId()==chat.getId())
                                return true;
                            else 
                                return false;
                        }
                    });
                    Chat newChat=chatService.save(chat);
                    if (newChat.getProfiles().size()==0)
                        chatService.delete(newChat);
                    return new ResponseEntity<Chat>(newChat,HttpStatus.ACCEPTED);
                }
                else
                    return new ResponseEntity<Chat>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Chat>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Chat>(HttpStatus.BAD_REQUEST);
   }
   
   @PostMapping(value = "/chat/{id}/messages")
   public ResponseEntity<Chat> postToChatMessage(@RequestBody Message message,@PathVariable @Min(1) Long id){
       Chat chat=chatService.findById(id);
        if (chat!=null && message!=null && message.getId()!=null){
            Message messageFromDB=messageService.findById(message.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            chatService.initializeProfile(chat);
            chatService.initializeMessages(chat);
            if (messageFromDB!=null){
                if (chat.contain(currentProfile) && !chat.contain(messageFromDB))
                {
                    deleteMessageFromOldChat(messageFromDB);
                    messageFromDB.setChat(chat);
                    chat.getMessages().add(messageFromDB);
                    return new ResponseEntity<Chat>(chatService.save(chat),HttpStatus.CREATED);
                }
                else
                    return new ResponseEntity<Chat>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Chat>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Chat>(HttpStatus.BAD_REQUEST);
   }
   
   @GetMapping(value="/chat/{id}/messages")
   public ResponseEntity<List<Message>> getMessagesFromChat(@PathVariable @Min(1) Long id){
       List<Message> messages=messageService.findByChatIdOrderByDate(id);
       return new ResponseEntity<List<Message>>(messages, HttpStatus.OK);
   }
   
   private void deleteMessageFromOldChat(Message messageFromDB){
       if (messageFromDB.getChat()!=null)
        {
         Chat oldChat=chatService.findById(messageFromDB.getChat().getId());
                oldChat.getMessages().removeIf((Message message1) -> {
                return message1.getId() == messageFromDB.getId();
            });
            chatService.save(oldChat);
        }
   }
   
   @DeleteMapping(value = "/chat/{id}/messages")
   public ResponseEntity<Chat> deleteMessageFromChat(@RequestBody Message message,@PathVariable @Min(1) Long id){
       Chat chat=chatService.findById(id);
        if (chat!=null && message!=null && message.getId()!=null){
            Message messageFromDB=messageService.findById(message.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            chatService.initializeMessages(chat);
            chatService.initializeProfile(chat);
            if (messageFromDB!=null){
                if (chat.contain(currentProfile) && messageFromDB.getSender().getId()==currentProfile.getId() && chat.contain(messageFromDB))
                {
                    chat.getMessages().removeIf(new Predicate<Message>() {
                        @Override
                        public boolean test(Message message) {
                            if (message.getId()==messageFromDB.getId())
                                return true;
                            else 
                                return false;
                        }
                    });
                    return new ResponseEntity<Chat>(chatService.save(chat),HttpStatus.ACCEPTED);
                }
                else
                    return new ResponseEntity<Chat>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Chat>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Chat>(HttpStatus.BAD_REQUEST);
   }
   
   @DeleteMapping(value = "/chat")
   public ResponseEntity<Chat> deleteChat(@RequestBody Chat chat){
       if (chat!=null && chat.getId()!=null)
       {
           Chat chatfromDB=chatService.findById(chat.getId());
           if (chatfromDB!=null)
           {
               Profile curProfile=getAuthenticatedProfileFromDB();
               if (chatfromDB.contain(curProfile))
                {
                    chatService.initializeProfile(chatfromDB);
                    for (Profile profile : chatfromDB.getProfiles()) {
                        profileService.initializeChats(profile);
                        profile.getChats().removeIf((Chat arg0) -> arg0.getId()==chatfromDB.getId());
                    }
                    chatfromDB.getProfiles().clear();
                    chatService.save(chatfromDB);
                    chatService.delete(chatfromDB);
                    return new ResponseEntity<Chat>(chatfromDB,HttpStatus.ACCEPTED);
                }
                else
                    return new ResponseEntity<Chat>(HttpStatus.FORBIDDEN);
           }
           else
               return new ResponseEntity<Chat>(HttpStatus.BAD_REQUEST);
       }
       else
           return new ResponseEntity<Chat>(HttpStatus.BAD_REQUEST);
   }
   
   @PatchMapping(value = "/chat")
   public ResponseEntity<Chat> patchChat(@Valid @RequestBody Chat chat){
       if (chat.getId()!=null)
       {
           Chat chatFromDB=chatService.findById(chat.getId());
           if (chatFromDB!=null){
               Profile currentProfile=getAuthenticatedProfileFromDB();
               if (chatFromDB.contain(currentProfile))
               {
                   chatFromDB.setTitle(chat.getTitle());
                   return new ResponseEntity<Chat>(chatService.save(chatFromDB),HttpStatus.ACCEPTED);
               }
               else
                  return new ResponseEntity<Chat>(HttpStatus.FORBIDDEN); 
           }
           else
               return new ResponseEntity<Chat>(HttpStatus.BAD_REQUEST);
       }
       else
           return new ResponseEntity<Chat>(HttpStatus.BAD_REQUEST);
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
    
}
