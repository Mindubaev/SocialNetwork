/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO.Topic;

import com.google.common.collect.Lists;
import com.socialNetwork.socialNetwork.DAO.Comment.CommentRepository;
import com.socialNetwork.socialNetwork.Entity.Comment;
import com.socialNetwork.socialNetwork.Entity.Topic;
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
@Service("topicService")
@Transactional
public class TopicServiceImpl implements TopicService{
    
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private CommentRepository commentRepository;

    public TopicServiceImpl(TopicRepository topicRepository, CommentRepository commentRepository) {
        this.topicRepository = topicRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Topic save(Topic topic) {
        return topicRepository.save(topic);
    }

    @Override
    public void delete(Topic topic) {
        topicRepository.delete(topic);
    }

    @Override
    public void deleteById(Long Id) {
        topicRepository.deleteById(Id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Topic> findByProfileId(Long id) {
        Optional<List<Topic>> optional=topicRepository.findByProfileId(id);
        if (optional.isPresent())
            return optional.get();
        else
            return new ArrayList<Topic>();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Topic> findAll() {
        List<Topic> list=Lists.newArrayList(topicRepository.findAll());
        if (list!=null)
            return list;
        else
            return new ArrayList<Topic>();
    }

    @Transactional(readOnly = true)
    @Override
    public Topic findById(Long id) {
        Optional<Topic> optional=topicRepository.findById(id);
        if (optional.isPresent())
            return optional.get();
        else
            return null;
    }

    @Override
    public boolean initializeComments(Topic topic) {
        Optional<List<Comment>> optional=commentRepository.findByTopicId(topic.getId());
        try{
            topic.getComments().size();
            topic.getComments().clear();
        }catch(Exception ex){
            topic.setComments(new ArrayList<Comment>());
        }
        if (optional.isPresent())
        {
            List<Comment> comments=optional.get();
            for (Comment comment:comments)
                topic.getComments().add(comment);
            return true;
        }
        else
            return false;
    }
    
    
}
