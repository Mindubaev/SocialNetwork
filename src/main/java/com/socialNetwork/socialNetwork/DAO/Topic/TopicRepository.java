/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO.Topic;

import com.socialNetwork.socialNetwork.Entity.Topic;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Artur
 */
public interface TopicRepository extends CrudRepository<Topic, Long>{
    
    Optional<List<Topic>> findByProfileId(Long id);
    
}
