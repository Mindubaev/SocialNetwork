/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Jackson.Serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.socialNetwork.socialNetwork.Entity.Comment;
import com.socialNetwork.socialNetwork.Entity.Profile;
import com.socialNetwork.socialNetwork.Entity.Topic;
import java.io.IOException;

/**
 *
 * @author Artur
 */
public class CommentSerializer extends StdSerializer<Comment>{

    public CommentSerializer(Class<Comment> c){
        super(c);
    }
    
    public CommentSerializer(){
        this(null);
    }
    
    @Override
    public void serialize(Comment comment, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        if (comment.getId()!=null)
            generator.writeNumberField("id", comment.getId());
        else
            generator.writeNumberField("id", -1);
        generator.writeStringField("text",comment.getText());
        generator.writeObjectField("date", comment.getDate());
        serializeProfile(comment, generator);
        serializeTopic(comment, generator);
        generator.writeEndObject();
    }
    
    private void serializeProfile(Comment comment, JsonGenerator generator) throws IOException {
        try {
            Profile profile = comment.getProfile();
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
            generator.writeObjectField("id", null);
            generator.writeStringField("name", null);
            generator.writeStringField("lastname", null);
            generator.writeObjectField("birthDate", null);
        }
    }
    
    private void serializeTopic(Comment comment, JsonGenerator generator) throws IOException {
        try {
            Topic topic = comment.getTopic();
            topic.getId();
            generator.writeObjectFieldStart("topic");
            if (topic.getId() != null) {
                generator.writeNumberField("id", topic.getId());
            } else {
                generator.writeNumberField("id", -1);
            }
            generator.writeStringField("text",topic.getText());
            generator.writeEndObject();
        } catch (Exception ex) {
            generator.writeStartObject();//!!
            generator.writeObjectField("id", null);
            generator.writeStringField("text",null);
            generator.writeEndObject();//!!
        }
    }
    
}
