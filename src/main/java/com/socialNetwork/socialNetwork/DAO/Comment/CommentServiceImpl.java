/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO.Comment;

import com.google.common.collect.Lists;
import com.socialNetwork.socialNetwork.Entity.Comment;
import com.socialNetwork.socialNetwork.Entity.Topic;
import java.util.ArrayList;
import java.util.Date;
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
@Service("commentService")
@Transactional
public class CommentServiceImpl implements CommentService{
    
    @Autowired
    private CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> findByDate(Date date) {
        Optional<List<Comment>> optional=commentRepository.findByDate(date);
        if (optional.isPresent())
            return optional.get();
        else
            return new ArrayList<Comment>();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> findByDateBetween(Date start, Date finish) {
        Optional<List<Comment>> optional=commentRepository.findByDateBetween(start, finish);
        if (optional.isPresent())
            return optional.get();
        else
            return new ArrayList<Comment>();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> findAll() {
        List<Comment> list=Lists.newArrayList(commentRepository.findAll());
        if (list!=null)
            return list;
        else 
            return new ArrayList<Comment>();
    }

    @Transactional(readOnly = true)
    @Override
    public Comment findById(Long id) {
        Optional<Comment> optional=commentRepository.findById(id);
        if (optional.isPresent())
            return optional.get();
        else
            return null;
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> findByProfileId(Long id) {
        Optional<List<Comment>> optional=commentRepository.findByProfileId(id);
        if (optional.isPresent())
            return optional.get();
        else
            return new ArrayList<Comment>();
    }

    @Override
    public List<Comment> findByTopicId(Long id) {
        Optional<List<Comment>> optional=commentRepository.findByTopicId(id);
        if (optional.isPresent())
            return optional.get();
        else
            return new ArrayList<Comment>();
    }
    
}
