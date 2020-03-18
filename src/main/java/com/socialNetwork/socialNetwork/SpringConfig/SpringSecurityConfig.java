/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.SpringConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 *
 * @author Artur
 */
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter{
    
    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsRepository);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().
                antMatchers(HttpMethod.GET, "/profile/**","/chat/**","/message/**","/topic/**","/comment/**").authenticated().
                antMatchers(HttpMethod.POST, "/profile/login/**","/profile/logout/**").permitAll().
                antMatchers(HttpMethod.POST, "/profile**").anonymous().
                antMatchers(HttpMethod.POST,"/chat","/message","/topic","/comment").permitAll().
                antMatchers(HttpMethod.PATCH, "/profile/**","/chat/**","/message/**","/topic/**","/comment/**").authenticated().
                antMatchers(HttpMethod.DELETE, "/profile/**","/chat/**","/message/**","/topic/**","/comment/**").authenticated().
                antMatchers("/h2-console/**").permitAll().
                and().csrf().disable().
                formLogin().and().logout().logoutUrl("/logout");
                http.headers().frameOptions().disable().and().cors();
    }

    public UserDetailsRepository getUserDetailsRepository() {
        return userDetailsRepository;
    }

    public void setUserDetailsRepository(UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }
    
    @Bean(name = "myAuthenticationManager")
    @Override
    public AuthenticationManager authenticationManager() throws Exception{
        return super.authenticationManager();
    }
    
}
