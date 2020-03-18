/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Controller;

import com.socialNetwork.socialNetwork.DAO.Comment.CommentService;
import com.socialNetwork.socialNetwork.DAO.Profile.ProfileService;
import com.socialNetwork.socialNetwork.DAO.Topic.TopicService;
import com.socialNetwork.socialNetwork.Entity.Comment;
import com.socialNetwork.socialNetwork.Entity.Profile;
import com.socialNetwork.socialNetwork.Entity.Topic;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Artur
 */
@RestController
@Validated
@CrossOrigin(origins = "http://localhost:8080",allowCredentials = "true",allowedHeaders = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PATCH,RequestMethod.DELETE})
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private TopicService topicService;
    
    @GetMapping("/comment/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable @Min(1) Long id){
        Comment commentFromDB=commentService.findById(id);
        if (commentFromDB!=null)
        {
            Profile currentProfile=getAuthenticatedProfileFromDB();
            if (currentProfile.getId()==commentFromDB.getProfile().getId())
            {
                return new ResponseEntity<Comment>(commentFromDB,HttpStatus.FOUND);
            }
            else
                return new ResponseEntity<Comment>(HttpStatus.FORBIDDEN);
        }
        else
            return new ResponseEntity<Comment>(HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/comment")
    public ResponseEntity<Comment> postComment(@RequestBody Comment comment){
        if (comment!=null && comment.getText()!=null && comment.getDate()!=null && comment.getProfile()!=null && comment.getTopic()!=null && comment.getProfile().getId()!=null && comment.getTopic().getId()!=null){
            Profile currentProfile=getAuthenticatedProfileFromDB();
            if (currentProfile.getId()==comment.getProfile().getId())
            {
                Topic topicFromDB=topicService.findById(comment.getTopic().getId());
                if (topicFromDB!=null)
                {
                    comment.setTopic(topicFromDB);
                    topicFromDB.getComments().add(comment);
                    comment.setProfile(currentProfile);
                    currentProfile.getComments().add(comment);
                    comment.setId(null);
                    return new ResponseEntity<Comment>(commentService.save(comment), HttpStatus.CREATED);
                }
                else
                    return new ResponseEntity<Comment>(HttpStatus.BAD_REQUEST);
            }
            else
                return new ResponseEntity<Comment>(HttpStatus.FORBIDDEN);
        }
        else
           return new ResponseEntity<Comment>(HttpStatus.BAD_REQUEST);
    }
    
    @PatchMapping("/comment")
    public ResponseEntity<Comment> patchComment(@RequestBody @Valid Comment comment){//изменить пользователя по логике приложения не возможно
        Comment commentFromDB=commentService.findById(comment.getId());
        if (commentFromDB!=null && comment.getProfile()!=null && comment.getTopic()!=null && comment.getProfile().getId()!=null && comment.getTopic().getId()!=null){
            Profile currentProfile=getAuthenticatedProfileFromDB();
            if (currentProfile.getId()==comment.getProfile().getId())
            {
                Topic topicFromDB=topicService.findById(comment.getTopic().getId());
                if (topicFromDB!=null)
                {
                    commentFromDB.setText(comment.getText());
                    commentFromDB.setDate(comment.getDate());
                    commentFromDB.setTopic(topicFromDB);
                    if (!topicFromDB.contain(commentFromDB))//!!Delete old comment from profile and topic
                        topicFromDB.getComments().add(commentFromDB);
                    commentFromDB.setProfile(currentProfile);
                    if (!currentProfile.contain(commentFromDB))
                        currentProfile.getComments().add(commentFromDB);//!!
                    return new ResponseEntity<Comment>(commentService.save(commentFromDB), HttpStatus.ACCEPTED);
                }
                else
                    return new ResponseEntity<Comment>(HttpStatus.BAD_REQUEST);
            }
            else
                return new ResponseEntity<Comment>(HttpStatus.FORBIDDEN);
        }
        else
           return new ResponseEntity<Comment>(HttpStatus.BAD_REQUEST);
    }
    
    @DeleteMapping("/comment")
    public ResponseEntity<Comment> deleteComment(@RequestBody Comment comment){
        if(comment!=null && comment.getId()!=null)
        {
            Comment commentFromDB=commentService.findById(comment.getId());
            if (commentFromDB!=null){
                Profile currentProfile=getAuthenticatedProfileFromDB();
                if (currentProfile.getId()==commentFromDB.getProfile().getId()){
    //                Topic topicFromDB=topicService.findById(commentFromDB.getTopic().getId());
    //                topicService.initializeComments(topicFromDB);
    //                currentProfile.getComments().removeIf(new Predicate<Comment>() {//!
    //                    @Override
    //                    public boolean test(Comment arg0) {
    //                        if (arg0.getId()==commentFromDB.getId())
    //                            return true;
    //                        else
    //                            return false;
    //                    }
    //                });
    //                topicFromDB.getComments().removeIf(new Predicate<Comment>() {//!
    //                    @Override
    //                    public boolean test(Comment arg0) {
    //                        if (arg0.getId()==commentFromDB.getId())
    //                            return true;
    //                        else
    //                            return false;
    //                    }
    //                });
                    commentFromDB.setProfile(null);//!
                    commentFromDB.setTopic(null);//!
                    commentService.delete(commentFromDB);
                    return new ResponseEntity<Comment>(HttpStatus.FOUND);
                }
                else
                    return new ResponseEntity<Comment>(HttpStatus.FORBIDDEN);
            }
            else
                return new ResponseEntity<Comment>(HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<Comment>(HttpStatus.BAD_REQUEST);
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
