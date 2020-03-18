/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Jackson.Serializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.socialNetwork.socialNetwork.Entity.Chat;
import com.socialNetwork.socialNetwork.Entity.Comment;
import com.socialNetwork.socialNetwork.Entity.Message;
import com.socialNetwork.socialNetwork.Entity.Profile;
import com.socialNetwork.socialNetwork.Entity.Topic;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Artur
 */
@JsonSerialize(using = ProfileWithPasswordSerializer.class)
public class ProfileForFullSerialization extends Profile{
    
    public ProfileForFullSerialization(){
    }
    
    public ProfileForFullSerialization(String name, String lastname, byte[] photo, Date birthDate, String login, String password) {
        super.setName(name);
        super.setLastname(lastname);
        super.setPhoto(photo);
        super.setBirthDate(birthDate);
        super.setLogin(login);
        super.setPassword(password);
    }
    
    public ProfileForFullSerialization(Long id, String name, String lastname, byte[] photo, Date birthDate, String login, String password, Set<Chat> chats, List<Topic> topics, List<Comment> comments, List<Message> messages) {
        super.setId(id);
        super.setName(name);
        super.setLastname(lastname);
        super.setPhoto(photo);
        super.setBirthDate(birthDate);
        super.setLogin(login);
        super.setPassword(password);
        super.setChats(chats);
        super.setTopics(topics);
        super.setComments(comments);
        super.setMessages(messages);
    }
    
    public ProfileForFullSerialization(Profile profile) {
        super.setId(profile.getId());
        super.setName(profile.getName());
        super.setLastname(profile.getLastname());
        super.setPhoto(profile.getPhoto());
        super.setBirthDate(profile.getBirthDate());
        super.setLogin(profile.getLogin());
        super.setPassword(profile.getPassword());
        super.setChats(profile.getChats());
        super.setTopics(profile.getTopics());
        super.setComments(profile.getComments());
        super.setMessages(profile.getMessages());
    }
    
}
