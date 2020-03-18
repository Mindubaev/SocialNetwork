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
import org.springframework.cglib.beans.BeanCopier;

/**
 *
 * @author Artur
 */
public class ChatSerializer extends StdSerializer<Chat>{
    
    public ChatSerializer(Class<Chat> c){
        super(c);
    }
    
    public ChatSerializer(){
        this(null);
    }

    @Override
    public void serialize(Chat chat, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        if (chat.getId()!=null)
            generator.writeNumberField("id", chat.getId());
        else
            generator.writeNumberField("id", -1);
        generator.writeStringField("title", chat.getTitle());
        serializeMessages(chat, generator);
        serializeProfiles(chat, generator);
        generator.writeEndObject();
    }
    
    private void serializeMessages(Chat chat,JsonGenerator generator) throws IOException{
        try{
            List<Message> messages=chat.getMessages();
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
    
    private void serializeProfiles(Chat chat,JsonGenerator generator) throws IOException{
        try{
            Set<Profile> profiles=chat.getProfiles();
            profiles.size();
            generator.writeArrayFieldStart("profiles");
            for (Profile profile:profiles)
            {
                generator.writeStartObject();
                if (profile.getId()!=null)
                    generator.writeNumberField("id", profile.getId());
                else
                    generator.writeNumberField("id", -1);
                generator.writeStringField("name", profile.getName());
                generator.writeStringField("lastname", profile.getLastname());
                generator.writeObjectField("birthDate", profile.getBirthDate());
                generator.writeEndObject();
            }
            generator.writeEndArray();
        }catch(Exception ex){
            generator.writeObjectField("profiles", new HashSet<Profile>());
        }
    }
    
}
