/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO.Profile;

import com.google.common.collect.Lists;
import com.socialNetwork.socialNetwork.DAO.Chat.ChatRepository;
import com.socialNetwork.socialNetwork.DAO.Chat.ChatService;
import com.socialNetwork.socialNetwork.DAO.Comment.CommentRepository;
import com.socialNetwork.socialNetwork.DAO.Message.MessageRepository;
import com.socialNetwork.socialNetwork.DAO.Topic.TopicRepository;
import com.socialNetwork.socialNetwork.Entity.Chat;
import com.socialNetwork.socialNetwork.Entity.Comment;
import com.socialNetwork.socialNetwork.Entity.Message;
import com.socialNetwork.socialNetwork.Entity.Profile;
import com.socialNetwork.socialNetwork.Entity.Topic;
import com.socialNetwork.socialNetwork.DAO.JdbcTemplateHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Artur
 */
@Repository
@Service("profileService")
@Transactional
public class ProfileServiceImpl implements ProfileService{
    
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    public JdbcTemplateHelper jdbcTemplateHelper;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private CommentRepository commentRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository, JdbcTemplateHelper jdbcTemplateHelper, ChatRepository chatRepository, TopicRepository topicRepository, MessageRepository messageRepository, CommentRepository commentRepository) {
        this.profileRepository = profileRepository;
        this.jdbcTemplateHelper = jdbcTemplateHelper;
        this.chatRepository = chatRepository;
        this.topicRepository = topicRepository;
        this.messageRepository = messageRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    @Override
    public void delete(Profile profile) {
        profileRepository.delete(profile);
    }

    @Transactional(readOnly = true)
    @Override
    public Profile findByLoginAndPassword(String login, String password) {
        Optional<Profile> optional=profileRepository.findByLoginAndPassword(login, password);
        if (optional.isPresent())
            return optional.get();
        else
            return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Profile> findAll() {
        List<Profile> list =Lists.newArrayList(profileRepository.findAll());
        if (list==null)
            return new ArrayList<Profile>();
        else
            return list;
    }

    @Transactional(readOnly = true)
    @Override
    public Profile findById(long id) {
        Optional<Profile> optional=profileRepository.findById(id);
        if (optional.isPresent())
            return optional.get();
        else
            return null;
    }

    @Override
    public void deleteById(Long id) {
        profileRepository.deleteById(id);
    }

    @Override
    public boolean initializeChats(Profile profile) {
        List<Long> identificators=jdbcTemplateHelper.findChatRelationsByProfileId(profile.getId());
        try{
            profile.getChats().size();
            profile.getChats().clear();
        }catch(Exception ex){
            profile.setChats(new HashSet<Chat>());
        }
        List<Chat> chats=new ArrayList<>();
        boolean isInitialize=true;
        for(Long id:identificators)
        {
            Optional<Chat> optional=chatRepository.findById(id);
            if (optional.isPresent())
            {
                Chat chat=optional.get();
                profile.getChats().add(chat);
            }
            else
                isInitialize=false;
        }
        return isInitialize;
    }

    @Override
    public boolean initializeTopics(Profile profile) {
     Optional<List<Topic>> optional=topicRepository.findByProfileId(profile.getId());
     try{
         profile.getTopics().size();
         profile.getTopics().clear();
     }catch(Exception ex){
         profile.setTopics(new ArrayList<Topic>());
     }
     if (optional.isPresent())
     {
        List<Topic> topics =optional.get();
        for (Topic topic:topics)
            profile.getTopics().add(topic);
        return true;
     }
     else
         return false;
    }

    @Override
    public boolean initializeComment(Profile profile) {
        Optional<List<Comment>> optional=commentRepository.findByProfileId(profile.getId());
        try{
            profile.getComments().size();
            profile.getComments().clear();
        }catch(Exception ex){
            profile.setComments(new ArrayList<Comment>());
        }
        if (optional.isPresent())
     {
        List<Comment> comments =optional.get();
        for (Comment comment:comments)
            profile.getComments().add(comment);
        return true;
     }
    else
         return false;
    }

    @Override
    public boolean initializeMessage(Profile profile) {
        Optional<List<Message>> optional=messageRepository.findBySenderIdOrderByDate(profile.getId());
        try{
            profile.getMessages().size();
            profile.getMessages().clear();
        }
        catch(Exception ex){
            profile.setMessages(new ArrayList<Message>());
        }
        if (optional.isPresent())
        {
            List<Message> messages =optional.get();
            for (Message message:messages)
               profile.getMessages().add(message);
            return true;
        }
        else
            return false;
    }

    @Transactional(readOnly = true)
    @Override
    public Profile findByLogin(String login) {
        Optional<Profile> optional=profileRepository.findByLogin(login);
        if (optional.isPresent())
            return optional.get();
        else
            return null;
    }
    
}
