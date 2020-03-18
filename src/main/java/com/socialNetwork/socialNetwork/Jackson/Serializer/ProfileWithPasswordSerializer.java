/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.Jackson.Serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.socialNetwork.socialNetwork.Entity.Chat;
import com.socialNetwork.socialNetwork.Entity.Comment;
import com.socialNetwork.socialNetwork.Entity.Message;
import com.socialNetwork.socialNetwork.Entity.Profile;
import com.socialNetwork.socialNetwork.Entity.Topic;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Artur
 */
public class ProfileWithPasswordSerializer extends StdSerializer<ProfileForFullSerialization>{
    
    public ProfileWithPasswordSerializer(Class<ProfileForFullSerialization> p){
        super(p);
    }
    
    public ProfileWithPasswordSerializer(){
        this(null);
    }
    
    @Override
    public void serialize(ProfileForFullSerialization profile, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        if (profile.getId()!=null)
            generator.writeNumberField("id", profile.getId());
        else
            generator.writeNumberField("id", -1);
        generator.writeStringField("name", profile.getName());
        generator.writeStringField("lastname", profile.getLastname());
        generator.writeObjectField("birthDate", profile.getBirthDate());
        generator.writeObjectField("login", profile.getLogin());
        generator.writeObjectField("password", profile.getPassword());
        serializeChats(profile, generator);
        serializeTopics(profile, generator);
        serializeMessages(profile, generator);
        serializeComments(profile, generator);
        generator.writeEndObject();
    }
    
    private void serializeComments(ProfileForFullSerialization profile,JsonGenerator generator) throws IOException{
        try{
            List<Comment> comments=profile.getComments();
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
                generator.writeEndObject();
            }
            generator.writeEndArray();
        }catch(Exception ex){
            generator.writeObjectField("comments", new HashSet<Comment>());
        }
    }
    
    private void serializeMessages(ProfileForFullSerialization profile,JsonGenerator generator) throws IOException{
        try{
            List<Message> messages=profile.getMessages();
            messages.size();
            generator.writeArrayFieldStart("messages");
            for (Message message:messages)
            {
                generator.writeStartObject();
                if (message.getId()!=null)
                    generator.writeNumberField("id", message.getId());
                else
                    generator.writeNumberField("id", -1);
                generator.writeStringField("text",message.getText());
                generator.writeObjectField("date", message.getDate());
                generator.writeEndObject();
            }
            generator.writeEndArray();
        }catch(Exception ex){
            generator.writeObjectField("messages", new HashSet<Message>());
        }
    }
    
    private void serializeChats(ProfileForFullSerialization profile,JsonGenerator generator) throws IOException{
        try{
            Set<Chat> chats=profile.getChats();
            chats.size();
            generator.writeArrayFieldStart("chats");
            for (Chat chat:chats)
            {
                generator.writeStartObject();
                if (chat.getId()!=null)
                    generator.writeNumberField("id", chat.getId());
                else
                    generator.writeNumberField("id", -1);
                generator.writeStringField("title", chat.getTitle());
                generator.writeEndObject();
            }
            generator.writeEndArray();
        }catch(Exception ex){
            generator.writeObjectField("chats", new HashSet<Chat>());
        }
    }
    
    private void serializeTopics(ProfileForFullSerialization profile,JsonGenerator generator) throws IOException{
        try{
            List<Topic> topics=profile.getTopics();
            topics.size();
            generator.writeArrayFieldStart("topics");
            for (Topic topic:topics)
            {
                generator.writeStartObject();
                if (topic.getId()!=null)
                    generator.writeNumberField("id", topic.getId());
                else
                    generator.writeNumberField("id", -1);
                generator.writeStringField("text",topic.getText());
                generator.writeEndObject();
            }
            generator.writeEndArray();
        }catch(Exception ex){
            generator.writeObjectField("topics", new HashSet<Topic>());
        }
    }
    
}
