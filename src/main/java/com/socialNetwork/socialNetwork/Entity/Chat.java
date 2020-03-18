/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.socialNetwork.socialNetwork.DAO.Profile.ProfileService;
import com.socialNetwork.socialNetwork.Jackson.Serializer.ChatSerializer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author user
 */
@JsonSerialize(using = ChatSerializer.class)
@Entity(name = "chat")
@Table(name = "chat")
public class Chat implements Serializable {

    private Long id;
    //@NotBlank(message = "blank title field in chat")
    private String title;
    private List<Message> messages=new ArrayList<>();
    private Set<Profile> profiles=new HashSet<>();

    public Chat() {
    }

    public Chat(String title) {
        this.title = title;
        this.profiles = profiles;
        this.messages = messages;
    }

    public Chat(String title, List<Message> messages, Set<Profile> profiles) {
        this.title = title;
        this.messages = messages;
        this.profiles = profiles;
    }

    public void addProfile(Profile profile){
        profiles.add(profile);
        profile.getChats().add(this);
    }
    
    @ManyToMany(mappedBy = "chats", fetch = FetchType.LAZY)
    public Set<Profile> getProfiles() {
        return profiles;
    }

    /**
     * @param profiles the profiles to set
     */
    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }

    @org.springframework.data.annotation.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    @OneToMany(mappedBy = "chat", orphanRemoval = true,cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.LAZY)
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean contain(Profile currentProfile) {
        for(Profile profile:this.getProfiles())
            if (currentProfile.getId()==profile.getId())
                return true;
        return false;
    }

    public boolean contain(Message currentMessage) {
        for(Message message:this.getMessages())
            if (currentMessage.getId()==message.getId())
                return true;
        return false;
    }
    
}
