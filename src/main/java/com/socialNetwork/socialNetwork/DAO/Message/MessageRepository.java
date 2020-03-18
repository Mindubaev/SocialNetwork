/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO.Message;

import com.socialNetwork.socialNetwork.Entity.Message;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Artur
 */
public interface MessageRepository extends CrudRepository<Message, Long>{
    
    Optional<List<Message>> findByChatIdOrderByDate(Long chatId);
    Optional<List<Message>> findBySenderIdOrderByDate(Long profileId);
    
}
