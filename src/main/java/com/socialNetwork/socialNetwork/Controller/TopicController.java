/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Controller;

import com.socialNetwork.socialNetwork.DAO.Comment.CommentService;
import com.socialNetwork.socialNetwork.DAO.Profile.ProfileService;
import com.socialNetwork.socialNetwork.DAO.Topic.TopicService;
import com.socialNetwork.socialNetwork.Entity.Chat;
import com.socialNetwork.socialNetwork.Entity.Comment;
import com.socialNetwork.socialNetwork.Entity.Message;
import com.socialNetwork.socialNetwork.Entity.Profile;
import com.socialNetwork.socialNetwork.Entity.Topic;
import java.util.List;
import java.util.function.Predicate;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
 * @author Artur
 */
@Validated
@RestController
@CrossOrigin(origins = "http://localhost:8080",allowCredentials = "true",allowedHeaders = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PATCH,RequestMethod.DELETE})
public class TopicController {
    
    @Autowired
    private TopicService topicService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private CommentService commentService;
    
    @PostMapping("/topic/{id}/comments")
    public ResponseEntity<Topic> postCommentsToTopic(@PathVariable @Min(1) Long id,@RequestBody Comment comment){
        Topic topic=topicService.findById(id);
        if (topic!=null){
            Comment commentFromDB=commentService.findById(comment.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            if (commentFromDB!=null){
                if (currentProfile.getId()==commentFromDB.getProfile().getId() && !topic.contain(comment))
                {
                    deleteMessageFromOldTopic(commentFromDB);
                    topic.getComments().add(commentFromDB);
                    commentFromDB.setTopic(topic);
                    return new ResponseEntity<Topic>(topicService.save(topic),HttpStatus.CREATED);
                }
                else
                    return new ResponseEntity<Topic>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Topic>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Topic>(HttpStatus.BAD_REQUEST);
    }
    
    private void deleteMessageFromOldTopic(Comment commentFromDB){
       if (commentFromDB.getTopic()!=null)
        {
         Topic oldTopic=topicService.findById(commentFromDB.getTopic().getId());
                oldTopic.getComments().removeIf((Comment comment1) -> {
                return comment1.getId() == commentFromDB.getId();
            });
            topicService.save(oldTopic);
        }
   }
    
   @DeleteMapping(value = "/topic/{id}/comments")
   public ResponseEntity<Topic> deleteCommentFromTopic(@RequestBody @Valid Comment comment,@PathVariable @Min(1) Long id){
       Topic topic=topicService.findById(id);
        if (topic!=null){
            Comment commentFromDB=commentService.findById(comment.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            if (commentFromDB!=null){
                if (currentProfile.getId()==comment.getProfile().getId() && topic.contain(comment))
                {
                    topic.getComments().removeIf(new Predicate<Comment>() {
                        @Override
                        public boolean test(Comment arg0) {
                            if (comment.getId()==commentFromDB.getId())
                                return true;
                            else 
                                return false;
                        }
                    });
                    commentService.delete(commentFromDB);//!
                    return new ResponseEntity<Topic>(topicService.save(topic),HttpStatus.CREATED);
                }
                else
                    return new ResponseEntity<Topic>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Topic>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Topic>(HttpStatus.BAD_REQUEST);
   }
    
    @GetMapping("/topic/{id}")
    public ResponseEntity<Topic> getTopic(@PathVariable @Min(1) Long id){
        Topic currentTopic=topicService.findById(id);
        if (currentTopic!=null)
        {
            return new ResponseEntity<Topic>(currentTopic,HttpStatus.FOUND);
        }
        else
            return new ResponseEntity<Topic>(HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping("/topic")
    public ResponseEntity<List<Topic>> getTopic(){//изменить на pageble
            List<Topic> topics=topicService.findAll();
            return new ResponseEntity<List<Topic>>(topics,HttpStatus.FOUND);
    }
    
    @PostMapping("/topic")
    public ResponseEntity<Topic> postTopic(@RequestBody Topic topic){      
        if (topic!=null && topic.getText()!=null && topic.getProfile()!=null && topic.getProfile().getId()!=null)
        {
            Profile currentProfile=getAuthenticatedProfileFromDB();
            if (topic.getProfile().getId()==currentProfile.getId())
            {
                topic.setId(null);
                topic.setProfile(currentProfile);
                currentProfile.getTopics().add(topic);
                return new ResponseEntity<Topic>(topicService.save(topic), HttpStatus.CREATED);
            }
            else
                return new ResponseEntity<Topic>(HttpStatus.FORBIDDEN);
        }
        else 
            return new ResponseEntity<Topic>(HttpStatus.BAD_REQUEST);
    }
    
    @DeleteMapping("/topic")
    public ResponseEntity<Topic> deleteTopic(@RequestBody Topic topic){
        if(topic!=null && topic.getId()!=null){
            Topic topicFromBD=topicService.findById(topic.getId());
            Profile currentProfile=getAuthenticatedProfileFromDB();
            if (topicFromBD!=null){
                if (topicFromBD.getProfile().getId()==currentProfile.getId())
                {
                    currentProfile.getTopics().removeIf((Topic arg0) -> arg0.getId()==topicFromBD.getId());
                    topicService.delete(topicFromBD);
                    return new ResponseEntity<Topic>(topicFromBD, HttpStatus.ACCEPTED);
                }
                else
                    return new ResponseEntity<Topic>(HttpStatus.FORBIDDEN); 
            }
            else
               return new ResponseEntity<Topic>(HttpStatus.BAD_REQUEST); 
        }
        else
            return new ResponseEntity<Topic>(HttpStatus.BAD_REQUEST); 
    }
    
    @PatchMapping("/topic")
    public ResponseEntity<Topic> patchTopic(@RequestBody @Valid Topic topic){
        if(topic.getId()!=null){
            Topic topicFromDB=topicService.findById(topic.getId());
            if (topicFromDB!=null)
            {
                Profile currentProfile=getAuthenticatedProfileFromDB();
                if (topicFromDB.getProfile().getId()==currentProfile.getId())
                {
                    topicFromDB.setText(topic.getText());
                    return new ResponseEntity<Topic>(topicService.save(topicFromDB),HttpStatus.ACCEPTED);
                }
                else
                    return new ResponseEntity<Topic>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Topic>(HttpStatus.BAD_REQUEST); 
        }
        else
            return new ResponseEntity<Topic>(HttpStatus.BAD_REQUEST); 
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
