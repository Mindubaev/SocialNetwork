/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO.Comment;

import com.socialNetwork.socialNetwork.Entity.Comment;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Artur
 */
public interface CommentService {
    
    List<Comment> findByDate(Date date);
    List<Comment> findByDateBetween(Date start,Date finish);
    List<Comment> findAll();
    Comment findById(Long id);
    Comment save(Comment comment);
    void delete(Comment comment);
    void deleteById(Long id);
    List<Comment> findByProfileId(Long id);
    List<Comment> findByTopicId(Long id);
    
}
