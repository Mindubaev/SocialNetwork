/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO.Chat;

import com.socialNetwork.socialNetwork.Entity.Chat;
import java.util.List;

/**
 *
 * @author Artur
 */
public interface ChatService {
    
    Chat save(Chat chat);
    void delete(Chat chat);
    Chat findById(Long id);
    List<Chat> findAll();
    void deleteById(Long id);
    public boolean initializeMessages(Chat chat);
    public boolean initializeProfile(Chat chat);
    
}
