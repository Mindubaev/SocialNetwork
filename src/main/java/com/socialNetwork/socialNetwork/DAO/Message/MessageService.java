/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO.Message;

import com.socialNetwork.socialNetwork.Entity.Message;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Artur
 */
public interface MessageService {
    
    List<Message> findByChatIdOrderByDate(Long chatId);
    List<Message> findBySenderIdOrderByDate(Long profileId);
    List<Message> findall();
    Message findById(Long id);
    Message save(Message message);
    void delete(Message message);
    void deleteById(Long id);
    
}
