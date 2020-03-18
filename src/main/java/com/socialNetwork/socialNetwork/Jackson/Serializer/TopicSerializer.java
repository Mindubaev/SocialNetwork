/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Jackson.Serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.socialNetwork.socialNetwork.DAO.Comment.CommentService;
import com.socialNetwork.socialNetwork.Entity.Chat;
import com.socialNetwork.socialNetwork.Entity.Comment;
import com.socialNetwork.socialNetwork.Entity.Profile;
import com.socialNetwork.socialNetwork.Entity.Topic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Artur
 */
public class TopicSerializer extends StdSerializer<Topic>{
    
    @Autowired
    private CommentService commentService;

    public TopicSerializer(Class<Topic> t){
        super(t);
    }
    
    public TopicSerializer(){
        this(null);
    }
    
    @Override
    public void serialize(Topic topic, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        if (topic.getId()!=null)
            generator.writeNumberField("id", topic.getId());
        else
            generator.writeNumberField("id", -1);
        generator.writeStringField("text",topic.getText());
        serializeComments(topic, generator);
        serializeProfile(topic, generator);
        generator.writeEndObject();
    }
    
    private void serializeProfile(Topic topic, JsonGenerator generator) throws IOException {
        try {
            Profile profile = topic.getProfile();
            profile.getName();
            generator.writeObjectFieldStart("profile");
            if (profile.getId() != null) {
                generator.writeNumberField("id", profile.getId());
            } else {
                generator.writeNumberField("id", -1);
            }
            generator.writeStringField("name", profile.getName());
            generator.writeStringField("lastname", profile.getLastname());
            generator.writeObjectField("birthDate", profile.getBirthDate());
            generator.writeEndObject();
        } catch (Exception ex) {
            generator.writeStartObject();//!!
            generator.writeObjectField("id", null);
            generator.writeStringField("name", null);
            generator.writeStringField("lastname", null);
            generator.writeObjectField("birthDate", null);
            generator.writeEndObject();//!!
        }
    }
    
    private void serializeComments(Topic topic, JsonGenerator generator) throws IOException {
        try{
            List<Comment> comments=topic.getComments();
            comments.size();
            generator.writeArrayFieldStart("comments");
            for (Comment comment:comments)
            {
                generator.writeStartObject();
                if (comment.getId()!=null)
                    generator.writeNumberField("id", comment.getId());
                else
                    generator.writeNumberField("id", -1);
                generator.writeStringField("text",comment.getText());
                generator.writeObjectField("date", comment.getDate());
                generator.writeObjectFieldStart("profile");//
                generator.writeNumberField("id", comment.getProfile().getId());//
                generator.writeStringField("name", comment.getProfile().getName());//
                generator.writeStringField("lastname", comment.getProfile().getLastname());//
                generator.writeEndObject();//
                generator.writeEndObject();
            }
            generator.writeEndArray();
        }catch(Exception ex){
            generator.writeObjectField("comments", new ArrayList<Comment>());
        }
    }
    
}
