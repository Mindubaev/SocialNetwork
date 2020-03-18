/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO.Message;

import com.google.common.collect.Lists;
import com.socialNetwork.socialNetwork.DAO.Chat.ChatRepository;
import com.socialNetwork.socialNetwork.DAO.Profile.ProfileRepository;
import com.socialNetwork.socialNetwork.Entity.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Artur
 */
@Repository
@Service("messageService")
@Transactional
public class MessageServiceImpl implements MessageService{

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ProfileRepository profileRepository;

    public MessageServiceImpl(MessageRepository messageRepository, ChatRepository chatRepository, ProfileRepository profileRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.profileRepository = profileRepository;
    }

    public MessageServiceImpl() {
    }
    
    @Transactional(readOnly = true)
    @Override
    public List<Message> findByChatIdOrderByDate(Long chatId) {
        Optional<List<Message>> optional=getMessageRepository().findByChatIdOrderByDate(chatId);
        if (optional.isPresent())
                return optional.get();
        else
            return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Message> findBySenderIdOrderByDate(Long profileId) {
        Optional<List<Message>> optional=getMessageRepository().findBySenderIdOrderByDate(profileId);
        if (optional.isPresent())
                return optional.get();
        else
            return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Message> findall() {
        List<Message> list=Lists.newArrayList(getMessageRepository().findAll());
        if (list!=null)
            return list;
        else
            return new ArrayList<Message>();
    }

    @Transactional(readOnly = true)
    @Override
    public Message findById(Long id) {
        Optional<Message> optional = getMessageRepository().findById(id);
        if (optional.isPresent())
            return optional.get();
        else
            return null;
    }

    @Override
    public Message save(Message message) {
        return getMessageRepository().save(message);
    }

    @Override
    public void delete(Message message) {
        getMessageRepository().delete(message);
    }

    @Override
    public void deleteById(Long id) {
        getMessageRepository().deleteById(id);
    }

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }

    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    
}
