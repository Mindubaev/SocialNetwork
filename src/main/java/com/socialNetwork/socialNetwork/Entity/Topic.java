/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.socialNetwork.socialNetwork.Jackson.Serializer.TopicSerializer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author user
 */
@JsonSerialize(using = TopicSerializer.class)
@Validated
@Table(name = "topic")
@Entity(name = "topic")
public class Topic implements Serializable{
    
    private Long id;
    @NotBlank
    private String text;
    @NotNull
    private Profile profile;
    @NotNull
    private List<Comment> comments=new ArrayList<>();

    public Topic() {
    }

    public Topic(String text) {
        this.text = text;
    }

    public Topic(Long id, String text, Profile profile) {
        this.id = id;
        this.text = text;
        this.profile = profile;
    }

    public void addComment(Comment comment){
        comments.add(comment);
        comment.setTopic(this);
    }
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="profile_id")
    public Profile getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
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

    @Column(name = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @OneToMany(mappedBy = "topic",
            cascade = CascadeType.ALL,
            orphanRemoval = true,fetch = FetchType.LAZY)
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    
    public boolean contain(Comment currentComment){
        for (Comment comment:getComments())
        {
            if (comment.getId()==currentComment.getId())
                return true;
        }
        return false;
    }
    
}
