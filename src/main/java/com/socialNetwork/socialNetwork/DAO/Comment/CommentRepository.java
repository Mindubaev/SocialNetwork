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
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Artur
 */
public interface CommentRepository extends CrudRepository<Comment, Long>{
    
    Optional<List<Comment>> findByDate(Date date);
    Optional<List<Comment>> findByDateBetween(Date start,Date finish);
    Optional<List<Comment>> findByProfileId(Long id);
    Optional<List<Comment>> findByTopicId(Long id);
    
}
