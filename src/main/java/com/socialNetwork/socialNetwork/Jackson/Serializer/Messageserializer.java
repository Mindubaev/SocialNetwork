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
import com.socialNetwork.socialNetwork.Entity.Message;
import com.socialNetwork.socialNetwork.Entity.Profile;
import java.io.IOException;

/**
 *
 * @author Artur
 */
public class Messageserializer extends StdSerializer<Message> {

    public Messageserializer(Class<Message> m) {
        super(m);
    }

    public Messageserializer() {
        this(null);
    }

    @Override
    public void serialize(Message message, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        if (message.getId() != null) {
            generator.writeNumberField("id", message.getId());
        } else {
            generator.writeNumberField("id", -1);
        }
        generator.writeStringField("text", message.getText());
        generator.writeObjectField("date", message.getDate());
        serializeProfile(message, generator);
        serializeChat(message, generator);
        generator.writeEndObject();
    }

    private void serializeProfile(Message message, JsonGenerator generator) throws IOException {
        try {
            Profile profile = message.getSender();
            generator.writeObjectFieldStart("sender");
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
            generator.writeObjectFieldStart("sender");//!!
            generator.writeObjectField("id", null);
            generator.writeStringField("name", null);
            generator.writeStringField("lastname", null);
            generator.writeObjectField("birthDate", null);
            generator.writeEndObject();//!!
        }
    }

    private void serializeChat(Message message, JsonGenerator generator) throws IOException {
        try {
            Chat chat=message.getChat();
            generator.writeObjectFieldStart("chat");
        if (chat.getId()!=null)
            generator.writeNumberField("id", chat.getId());
        else
            generator.writeNumberField("id", -1);
            generator.writeStringField("title", chat.getTitle());
            generator.writeEndObject();
        } catch (Exception ex) {
            generator.writeStartObject();//!!
            generator.writeObjectField("id", null);
            generator.writeStringField("title", null);
            generator.writeEndObject();//!!
        }
    }

}
