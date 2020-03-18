/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Controller;

import com.socialNetwork.socialNetwork.DAO.Chat.ChatService;
import com.socialNetwork.socialNetwork.DAO.Comment.CommentService;
import com.socialNetwork.socialNetwork.DAO.Message.MessageService;
import com.socialNetwork.socialNetwork.DAO.Profile.ProfileService;
import com.socialNetwork.socialNetwork.DAO.Topic.TopicService;
import com.socialNetwork.socialNetwork.Entity.Chat;
import com.socialNetwork.socialNetwork.Entity.Comment;
import com.socialNetwork.socialNetwork.Entity.Message;
import com.socialNetwork.socialNetwork.Entity.Profile;
import com.socialNetwork.socialNetwork.Entity.Topic;
import com.socialNetwork.socialNetwork.Jackson.Serializer.ProfileForFullSerialization;
import java.util.function.Predicate;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Artur
 */
@RestController
@Validated
@CrossOrigin(origins = "http://localhost:8080",allowCredentials = "true",allowedHeaders = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PATCH,RequestMethod.DELETE})
public class ProfileController {
    
    @Autowired
    private ProfileService profileService;
    @Autowired
    private AuthenticationManager myAuthenticationManager;
    @Autowired
    private ChatService chatService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private CommentService commentService;
    
    @PostMapping(value = "/profile/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Profile> loginProfile(@RequestHeader("Authentication") String usernameAndPassword){
        String userPassword[]=usernameAndPassword.split(":");
        String username=userPassword[0].trim();
        String password=userPassword[1].trim();
        try{
            Authentication authentication=myAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            if (authentication.isAuthenticated())
            {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return new ResponseEntity<Profile>(profileService.findByLoginAndPassword(username, password), HttpStatus.OK);
            }
            else
                return new ResponseEntity<Profile>(HttpStatus.FORBIDDEN);
        }
        catch(Exception exception){
            return new ResponseEntity<Profile>(HttpStatus.FORBIDDEN);
        }
    }
    
    @PostMapping(value = "/profile/logout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Profile> logoutProfile(){
            SecurityContextHolder.getContext().setAuthentication(null);
            return new ResponseEntity<Profile>(HttpStatus.OK);
    } 
    
    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping(value="/profile/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Profile> GetPrifileById(@PathVariable @Min(1) Long id) throws ProfileGetFailedException{
        Profile profile=profileService.findById(id);
        if (profile!=null){
            if (hasAcces(profile))
            {
                return new ResponseEntity<Profile>(new ProfileForFullSerialization(profile),HttpStatus.FOUND);
            }
            else 
                return new ResponseEntity<Profile>(profile,HttpStatus.FOUND);
        }
        else
            return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
    }
    
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST,value = "/profile")
    public ResponseEntity<Profile> postProfile(@RequestBody @Valid Profile profile) throws ProfilePostFailedException{
        if (profile!=null )
        {
            profile.setId(null);
            return new ResponseEntity<Profile>(profileService.save(profile),HttpStatus.CREATED);
        }
        else
            return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
    } 
    
    private boolean isAlreadyexist(Long id){
        Profile profile=profileService.findById(id);
        if (profile!=null)
            return true;
        else 
            return false;
    }
    
    @PatchMapping("/profile")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Profile> patchProfile(@RequestBody @Valid Profile profile) throws ProfilePatchFailedException{
        if (profile!=null && hasAcces(profile)){
            return new ResponseEntity<Profile>(fullProfileUpdate(profile), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<Profile>(HttpStatus.FORBIDDEN);
    }
    
    @DeleteMapping("/profile")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Profile> deleteProfile(@RequestBody Profile profile) throws ProfileDeleteFailedException{
        if (profile!=null && profile.getId()!=null)
        {
            Profile currentProfile = getAuthenticatedProfileFromDB();
            if(currentProfile.getId()==profile.getId()){
                SecurityContextHolder.getContext().setAuthentication(null);
                profileService.deleteById(currentProfile.getId());
                return new ResponseEntity<Profile>(currentProfile,HttpStatus.ACCEPTED);
            }
            else
                return new ResponseEntity<Profile>(HttpStatus.FORBIDDEN);
        }
        else
            return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
    }
    
    @DeleteMapping("/profile/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void  deleteProfileById(@PathVariable @Min(1) long id) throws ProfileDeleteFailedException{
            profileService.deleteById(id);
    }
    
    
    private Profile fullProfileUpdate(Profile profile){
        if (profile!=null && profile.getId()!=null && profile.getId()>0 && profile.getName()!=null && profile.getLastname()!=null && profile.getBirthDate()!=null && profile.getLogin()!=null && profile.getPassword()!=null)
        {
            Profile profileFromBD=profileService.findById(profile.getId());
            if (profileFromBD!=null)
            {
                profileFromBD.setName(profile.getName());
                profileFromBD.setLastname(profile.getLastname());
                profileFromBD.setBirthDate(profile.getBirthDate());
                profileFromBD.setLogin(profile.getLogin());
                profileFromBD.setPassword(profile.getPassword());
                profileFromBD.setPhoto(profile.getPhoto());
                profileFromBD=profileService.save(profileFromBD);
                return profileFromBD;
            }
            else
                return null;
        }
        return null;
    }
    
    private boolean hasAcces(Profile profile){
        Profile profile1=getAuthenticatedProfileFromDB();
        if (profile1!=null && profile.getId()==profile1.getId())
            return true;
        else
            return false;
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
    
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class ProfilePostFailedException extends Exception{

        public ProfilePostFailedException(String message) {
            super(message);
        }
           
    }
    
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ProfileGetFailedException extends Exception{

        public ProfileGetFailedException(String message) {
            super(message);
        }
           
    }
    
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class ProfilePatchFailedException extends Exception{

        public ProfilePatchFailedException(String message) {
            super(message);
        }
           
    }
    
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ProfileDeleteFailedException extends Exception{

        public ProfileDeleteFailedException(String message) {
            super(message);
        }
           
    }
    
   @PostMapping(value = "/profile/{id}/messages")
   public ResponseEntity<Profile> postToProfileMessage(@RequestBody Message message,@PathVariable @Min(1) Long id){
       Profile profile=profileService.findById(id);
        if (profile!=null && message!=null && message.getId()!=null){
            Message messageFromDB=messageService.findById(message.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            profileService.initializeMessage(currentProfile);
            if (messageFromDB!=null){
                if (currentProfile.getId()==profile.getId() && !currentProfile.contain(messageFromDB))
                {
                    deleteMessageFromOldProfile(messageFromDB);
                    currentProfile.getMessages().add(messageFromDB);
                    messageFromDB.setSender(currentProfile);
                    return new ResponseEntity<Profile>(profileService.save(currentProfile),HttpStatus.CREATED);
                }
                else
                    return new ResponseEntity<Profile>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
   }
    
   @PostMapping(value = "/profile/{id}/topics")
   public ResponseEntity<Profile> postToProfileTopic(@RequestBody Topic topic,@PathVariable @Min(1) Long id){
       Profile profile=profileService.findById(id);
        if (profile!=null && topic!=null && topic.getId()!=null){
            Topic topicFromDB=topicService.findById(topic.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            profileService.initializeTopics(currentProfile);
            if (topicFromDB!=null){
                if (currentProfile.getId()==profile.getId() && !currentProfile.contain(topicFromDB))
                {
                    deleteTopicFromOldProfile(topicFromDB);
                    currentProfile.getTopics().add(topicFromDB);
                    topicFromDB.setProfile(currentProfile);
                    return new ResponseEntity<Profile>(profileService.save(currentProfile),HttpStatus.CREATED);
                }
                else
                    return new ResponseEntity<Profile>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
   }
   
   @PostMapping(value = "/profile/{id}/comments")
   public ResponseEntity<Profile> postToProfileComment(@RequestBody Comment comment,@PathVariable @Min(1) Long id){
       Profile profile=profileService.findById(id);
        if (profile!=null && comment!=null && comment.getId()!=null){
            Comment commentFromDB=commentService.findById(comment.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            profileService.initializeComment(currentProfile);
            if (commentFromDB!=null){
                if (currentProfile.getId()==profile.getId() && !currentProfile.contain(commentFromDB))
                {
                    deleteCommentFromOldProfile(commentFromDB);
                    currentProfile.getComments().add(commentFromDB);
                    commentFromDB.setProfile(currentProfile);
                    return new ResponseEntity<Profile>(profileService.save(currentProfile),HttpStatus.CREATED);
                }
                else
                    return new ResponseEntity<Profile>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
   }
   
   @PostMapping(value = "/profile/{id}/chats")
   public ResponseEntity<Profile> postToProfileChat(@RequestBody Chat chat,@PathVariable @Min(1) Long id){
       Profile profile=profileService.findById(id);
        if (profile!=null && chat!=null && chat.getId()!=null){
            Chat chatFromDB=chatService.findById(chat.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            profileService.initializeChats(currentProfile);
            if (chatFromDB!=null){
                chatService.initializeProfile(chatFromDB);
                if (currentProfile.getId()==profile.getId() && !currentProfile.contain(chatFromDB) && !chatFromDB.contain(currentProfile))
                {
                    currentProfile.getChats().add(chatFromDB);
                    chatFromDB.getProfiles().add(currentProfile);
                    return new ResponseEntity<Profile>(profileService.save(currentProfile),HttpStatus.CREATED);
                }
                else
                    return new ResponseEntity<Profile>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
   }
   
   private void deleteMessageFromOldProfile(Message messageFromDB){
       if (messageFromDB.getSender()!=null)
        {
         Profile oldProfile=profileService.findById(messageFromDB.getSender().getId());
                oldProfile.getMessages().removeIf((Message message1) -> {
                return message1.getId() == messageFromDB.getId();
            });
            profileService.save(oldProfile);
        }
   }
   
   private void deleteTopicFromOldProfile(Topic topicFromDB){
       if (topicFromDB.getProfile()!=null)
        {
         Profile oldProfile=profileService.findById(topicFromDB.getProfile().getId());
                oldProfile.getTopics().removeIf((Topic topic1) -> {
                return topic1.getId() == topicFromDB.getId();
            });
            profileService.save(oldProfile);
        }
   }
   
   private void deleteCommentFromOldProfile(Comment commentFromDB){
       if (commentFromDB.getProfile()!=null)
        {
         Profile oldProfile=profileService.findById(commentFromDB.getProfile().getId());
                oldProfile.getComments().removeIf((Comment comment1) -> {
                return comment1.getId() == commentFromDB.getId();
            });
            profileService.save(oldProfile);
        }
   }
   
//   private void deleteChatFromOldProfiles(Chat chatFromDB){
//       if (chatFromDB.getProfiles()!=null)
//        {
//         for(Profile p:chatFromDB.getProfiles()){
//            Profile oldProfile=profileService.findById(p.getId());
//            oldProfile.getChats().removeIf((Chat chat1) -> {
//                return chat1.getId() == chatFromDB.getId();
//            });
//            profileService.save(oldProfile);
//        }
//        }
//   }
   
   @DeleteMapping(value = "/profile/{id}/messages")
   public ResponseEntity<Profile> deleteMessageFromProfile(@RequestBody Message message,@PathVariable @Min(1) Long id){
       Profile profile=profileService.findById(id);
        if (profile!=null && message!=null && message!=null){
            Message messageFromDB=messageService.findById(message.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            profileService.initializeMessage(currentProfile);
            if (messageFromDB!=null){
                if (profile.getId()==currentProfile.getId() && profile.contain(messageFromDB))
                {
                    currentProfile.getMessages().removeIf(new Predicate<Message>() {
                        @Override
                        public boolean test(Message message) {
                            if (message.getId()==messageFromDB.getId())
                                return true;
                            else 
                                return false;
                        }
                    });
                    return new ResponseEntity<Profile>(profileService.save(currentProfile),HttpStatus.ACCEPTED);
                }
                else
                    return new ResponseEntity<Profile>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
   }
   
   @DeleteMapping(value = "/profile/{id}/topics")
   public ResponseEntity<Profile> deleteTopicFromProfile(@RequestBody Topic topic,@PathVariable @Min(1) Long id){
       Profile profile=profileService.findById(id);
        if (profile!=null && topic!=null && topic.getId()!=null){
            Topic topicFromDB=topicService.findById(topic.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            profileService.initializeTopics(currentProfile);
            if (topicFromDB!=null){
                if (profile.getId()==currentProfile.getId() && profile.contain(topicFromDB))
                {
                    currentProfile.getTopics().removeIf(new Predicate<Topic>() {
                        @Override
                        public boolean test(Topic topic) {
                            if (topic.getId()==topicFromDB.getId())
                                return true;
                            else 
                                return false;
                        }
                    });
                    return new ResponseEntity<Profile>(profileService.save(currentProfile),HttpStatus.ACCEPTED);
                }
                else
                    return new ResponseEntity<Profile>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
   }
   
   @DeleteMapping(value = "/profile/{id}/comments")
   public ResponseEntity<Profile> deleteCommentFromProfile(@RequestBody Comment comment,@PathVariable @Min(1) Long id){
       Profile profile=profileService.findById(id);
        if (profile!=null && comment!=null && comment.getId()!=null){
            Comment commentFromDB=commentService.findById(comment.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            profileService.initializeComment(currentProfile);
            if (commentFromDB!=null){
                if (profile.getId()==currentProfile.getId() && profile.contain(commentFromDB))
                {
                    currentProfile.getComments().removeIf(new Predicate<Comment>() {
                        @Override
                        public boolean test(Comment comment) {
                            if (comment.getId()==commentFromDB.getId())
                                return true;
                            else 
                                return false;
                        }
                    });
                    return new ResponseEntity<Profile>(profileService.save(currentProfile),HttpStatus.ACCEPTED);
                }
                else
                    return new ResponseEntity<Profile>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
   }
   
   @DeleteMapping(value = "/profile/{id}/chats")
   public ResponseEntity<Profile> deleteChatFromProfile(@RequestBody Chat chat,@PathVariable @Min(1) Long id){
       Profile profile=profileService.findById(id);
        if (profile!=null && chat!=null && chat.getId()!=null){
            Chat chatFromDB=chatService.findById(chat.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            profileService.initializeChats(currentProfile);
            if (chatFromDB!=null){
                chatService.initializeProfile(chatFromDB);
                if (profile.getId()==currentProfile.getId() && profile.contain(chatFromDB) && chatFromDB.contain(currentProfile))
                {
                    currentProfile.getChats().removeIf(new Predicate<Chat>() {
                        @Override
                        public boolean test(Chat chat) {
                            if (chat.getId()==chatFromDB.getId())
                                return true;
                            else 
                                return false;
                        }
                    });
                    chatFromDB.getProfiles().removeIf(new Predicate<Profile>() {
                        @Override
                        public boolean test(Profile profile) {
                            if (profile.getId()==currentProfile.getId())
                                return true;
                            else 
                                return false;
                        }
                    });
                    return new ResponseEntity<Profile>(profileService.save(currentProfile),HttpStatus.ACCEPTED);
                }
                else
                    return new ResponseEntity<Profile>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Profile>(HttpStatus.BAD_REQUEST);
   }
   
}
