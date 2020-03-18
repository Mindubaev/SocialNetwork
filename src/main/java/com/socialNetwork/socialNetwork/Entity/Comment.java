/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.socialNetwork.socialNetwork.Jackson.Serializer.CommentSerializer;
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
import javax.persistence.Table;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author user
 */
@JsonSerialize(using = CommentSerializer.class)
@Table(name="comment")
@Entity(name = "comment")
public class Comment implements Serializable{

    private Long id;
    @NotBlank
    private String text;
    private Profile profile;
    @NotNull
    private Date date;
    private Topic topic;

    public Comment() {
    }

    public Comment(String text,Date date) {
        this.id = id;
        this.text = text;
        this.date = date;
    }

    public Comment(Long id, String text, Profile profile, Date date, Topic topic) {
        this.id = id;
        this.text = text;
        this.profile = profile;
        this.date = date;
        this.topic = topic;
    }
    
    @org.springframework.data.annotation.Id
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @Column(name = "text")
    public String getText() {
        return text;
    }

    @ManyToOne
    @JoinColumn(name = "profile_id")
    public Profile getProfile() {
        return profile;
    }

    @Column(name = "date")
    @javax.persistence.Temporal(TemporalType.DATE)
    public Date getDate() {
        return date;
    }

    @ManyToOne()
    @JoinColumn(name = "topic_id")
    public Topic getTopic() {
        return topic;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
    
}
