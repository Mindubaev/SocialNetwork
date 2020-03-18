/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO.Profile;

import com.socialNetwork.socialNetwork.Entity.Profile;
import java.util.List;

/**
 *
 * @author Artur
 */
public interface ProfileService {
    
    Profile save(Profile profile);
    void delete(Profile profile);
    void deleteById(Long id);
    Profile findByLoginAndPassword(String login,String password);
    List<Profile> findAll();
    Profile findById(long id);
    Profile findByLogin(String login);
    boolean initializeChats(Profile profile);
    boolean initializeTopics(Profile profile);
    boolean initializeComment(Profile profile);
    boolean initializeMessage(Profile profile);
    
}
