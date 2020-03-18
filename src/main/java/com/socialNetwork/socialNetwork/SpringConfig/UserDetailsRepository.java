/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.SpringConfig;

import com.socialNetwork.socialNetwork.DAO.Profile.ProfileRepository;
import com.socialNetwork.socialNetwork.DAO.Profile.ProfileService;
import com.socialNetwork.socialNetwork.Entity.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Artur
 */
@Service("userDetailsRepository")
public class UserDetailsRepository implements UserDetailsService{
    
    @Autowired
    private ProfileService profileService;

    public UserDetailsRepository() {
    }

    public UserDetailsRepository(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Profile profile=profileService.findByLogin(login);
        if (profile!=null)
        {
            UserDetails user=User.withDefaultPasswordEncoder().username(login).password(profile.getPassword()).roles("USER").build();;
            return user;
        }
        else
           throw new UsernameNotFoundException("User with username:"+login+" is not found"); 
    }
    
}
