/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.SpringConfig;

import javax.sql.DataSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author Artur
 */
@Configuration
@ComponentScan(basePackages = "com.socialNetwork.socialNetwork")
@EnableTransactionManagement
//@EnableJpaRepositories(basePackages = "com.socialNetwork.socialNetwork" )
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class SpringRoot {
    
    @Bean
public PhysicalNamingStrategy physical() {
    return new PhysicalNamingStrategyStandardImpl();
}
 
@Bean
public ImplicitNamingStrategy implicit() {
    return new ImplicitNamingStrategyLegacyJpaImpl();
}
    
}
