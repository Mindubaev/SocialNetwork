/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO.Profile;

import com.socialNetwork.socialNetwork.Entity.Chat;
import com.socialNetwork.socialNetwork.Entity.Profile;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Artur
 */
public interface ProfileRepository extends CrudRepository<Profile, Long>{
    
    Optional<Profile> findByLoginAndPassword(String login,String password);
    Optional<Profile> findByLogin(String login);
    
}
