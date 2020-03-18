/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.socialNetwork.socialNetwork.Jackson.Serializer.Messageserializer;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author user
 */
@Validated
@JsonSerialize(using = Messageserializer.class)
@Table(name = "message")
@Entity(name = "message")
public class Message implements Serializable{

    private Long id;
    @NotNull
    private Chat chat;
    @NotNull
    private Profile sender;
    @NotBlank
    private String text;
    @NotNull
    private Date date;
    public Message() {
    }

    public Message(String text, Date date) {
        this.id = id;
        this.text = text;
        this.date = date;
    }

    public Message(Chat chat, Profile sender, String text, Date date) {
        this.id = id;
        this.chat = chat;
        this.sender = sender;
        this.text = text;
        this.date = date;
    }
    
    @org.springframework.data.annotation.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    public Long getId() {
        return id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id")
    public Chat getChat() {
        return chat;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    public Profile getSender() {
        return sender;
    }

    @Column(name="text")
    public String getText() {
        return text;
    }

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    public Date getDate() {
        return date;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public void setSender(Profile sender) {
        this.sender = sender;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
}
