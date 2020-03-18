/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.socialNetwork.socialNetwork.Jackson.Serializer.ProfileSerializer;
import com.socialNetwork.socialNetwork.Jackson.Serializer.ProfileWithPasswordSerializer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.transaction.TransactionScoped;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author user
 */
@JsonSerialize(using = ProfileSerializer.class)
@Table(name = "profile")
@Entity(name = "profile")
public class Profile implements Serializable {
    
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String lastname;
    private byte[] photo;
    @NotNull
    private Date birthDate;
    private String login;
    private String password;
    private Set<Chat> chats=new HashSet<>();
    private List<Topic> topics=new ArrayList<>();
    private List<Comment> comments=new ArrayList<>();
    private List<Message> messages=new ArrayList<>(); 

    public Profile() {
    }

    public Profile(String name, String lastname, byte[] photo, Date birthDate, String login, String password) {
        this.name = name;
        this.lastname = lastname;
        this.photo = photo;
        this.birthDate = birthDate;
        this.login = login;
        this.password = password;
    }

    public Profile(Long id, String name, String lastname, byte[] photo, Date birthDate, String login, String password, Set<Chat> chats, List<Topic> topics, List<Comment> comments, List<Message> messages) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.photo = photo;
        this.birthDate = birthDate;
        this.login = login;
        this.password = password;
        this.chats = chats;
        this.topics = topics;
        this.comments = comments;
        this.messages = messages;
    }
    
    public void addChat(Chat chat){
        chats.add(chat);
        chat.getProfiles().add(this);
    }
    
    public void addTopic(Topic topic){
        topics.add(topic);
        topic.setProfile(this);
    }
    
    public void addComment(Comment comment){
        comments.add(comment);
        comment.setProfile(this);
    }
    
    public void addMessage(Message message){
        messages.add(message);
        message.setSender(this);
    }
    
    @org.springframework.data.annotation.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "lastname")
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname the lastname to set
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Column(name = "photo")
    @Lob
    public byte[] getPhoto() {
        return photo;
    }

    /**
     * @param photo the photo to set
     */
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    @Column(name = "birth_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * @param birthDate the birthDate to set
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @Column(name = "login")
    public String getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        this.login = login;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinTable(name = "chat_profile_relation",
            joinColumns = @JoinColumn(name="profile_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id"))
    public Set<Chat> getChats() {
        return chats;
    }

    /**
     * @param chats the chats to set
     */
    public void setChats(Set<Chat> chats) {
        this.chats = chats;
    }

    @OneToMany(mappedBy = "profile",
            cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},
            orphanRemoval = true,fetch = FetchType.LAZY)
    public List<Topic> getTopics() {
        return topics;
    }

    /**
     * @param topics the topics to set
     */
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    @OneToMany(mappedBy = "profile",
            cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},
            orphanRemoval = true,fetch = FetchType.LAZY)
    public List<Comment> getComments() {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @OneToMany(mappedBy = "sender",
            cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},
            orphanRemoval = true)
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    public boolean contain(Message currentMessage){
        for (Message message:getMessages())
        {
            if (message.getId()==currentMessage.getId())
                return true;
        }
        return false;
    }
    
    public boolean contain(Topic currentTopic){
        for (Topic topic:getTopics())
        {
            if (topic.getId()==currentTopic.getId())
                return true;
        }
        return false;
    }
    
    public boolean contain(Comment currentComment){
        for (Comment comment:getComments())
        {
            if (comment.getId()==currentComment.getId())
                return true;
        }
        return false;
    }
    
    public boolean contain(Chat currentChat){
        for (Chat chat:getChats())
        {
            if (chat.getId()==currentChat.getId())
                return true;
        }
        return false;
    }
    
}
