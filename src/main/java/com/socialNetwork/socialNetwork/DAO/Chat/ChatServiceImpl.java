/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO.Chat;

import com.google.common.collect.Lists;
import com.socialNetwork.socialNetwork.DAO.Message.MessageRepository;
import com.socialNetwork.socialNetwork.DAO.Message.MessageService;
import com.socialNetwork.socialNetwork.DAO.Profile.ProfileRepository;
import com.socialNetwork.socialNetwork.DAO.Profile.ProfileService;
import com.socialNetwork.socialNetwork.Entity.Chat;
import com.socialNetwork.socialNetwork.Entity.Message;
import com.socialNetwork.socialNetwork.Entity.Profile;
import com.socialNetwork.socialNetwork.DAO.JdbcTemplateHelper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Artur
 */
@Repository
@Service("chatService")
public class ChatServiceImpl implements ChatService{

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private JdbcTemplateHelper jdbcTemplateHelper;

    public ChatServiceImpl(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public ChatServiceImpl(ChatRepository chatRepository, ProfileRepository profileRepository, MessageRepository messageRepository, JdbcTemplateHelper jdbcTemplateHelper) {
        this.chatRepository = chatRepository;
        this.profileRepository = profileRepository;
        this.messageRepository = messageRepository;
        this.jdbcTemplateHelper = jdbcTemplateHelper;
    }
    
    public ChatServiceImpl() {
    }

    @Override
    public Chat save(Chat chat) {
        return chatRepository.save(chat);
    }

    @Override
    public void delete(Chat chat) {
        chatRepository.delete(chat);
    }

    @Transactional(readOnly = true)
    @Override
    public Chat findById(Long id) {
        Optional<Chat> optional=chatRepository.findById(id);
        if (optional.isPresent())
            return optional.get();
        else
            return null;
    }

    @Override
    public void deleteById(Long id) {
        chatRepository.deleteById(id);
    }

    @Override
    public List<Chat> findAll() {
        List<Chat> list=Lists.newArrayList(chatRepository.findAll());
        if (list==null)
            return new ArrayList<Chat>();
        else
            return list;
    }
    
    public boolean initializeProfile(Chat chat){
        List<Long> identificators=jdbcTemplateHelper.findProfileRelationsByChatId(chat.getId());
        try{
            chat.getProfiles().size();
            chat.getProfiles().clear();
        }catch(Exception ex){
            chat.setProfiles(new HashSet<Profile>());
        }
        List<Chat> profiles=new ArrayList<>();
        boolean isInitialize=true;
        for(Long id:identificators)
        {
            Optional<Profile> optional=profileRepository.findById(id);
            if (optional.isPresent())
            {
                Profile profile=optional.get();
                chat.getProfiles().add(profile);
            }
            else
                isInitialize=false;
        }
        return isInitialize;
    }
    
    public boolean initializeMessages(Chat chat){
     Optional<List<Message>> optional=messageRepository.findByChatIdOrderByDate(chat.getId());
     try 
     {
        chat.getMessages().size();
        chat.getMessages().clear();
     }
     catch(Exception ex){
        chat.setMessages(new ArrayList<Message>());
     }
     if (optional.isPresent())
     {
        List<Message> messages =optional.get();
        for (Message message:messages)
            chat.getMessages().add(message);
        return true;
     }
     else
         return false;
    }
    
}
