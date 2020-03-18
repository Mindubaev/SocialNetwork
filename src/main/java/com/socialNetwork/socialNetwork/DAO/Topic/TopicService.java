/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO.Topic;

import com.socialNetwork.socialNetwork.Entity.Topic;
import java.util.List;

/**
 *
 * @author Artur
 */
public interface TopicService {
    
    Topic save(Topic topic);
    void delete(Topic topic);
    void deleteById(Long Id);
    List<Topic> findByProfileId(Long id);//!!
    List<Topic> findAll();
    Topic findById(Long id);
    boolean initializeComments(Topic topic);
    
}
