/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialNetwork.socialNetwork.DAO;

import java.security.interfaces.RSAKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 *
 * @author Artur
 */
@Service("jdbcTemplateHelper")
public class JdbcTemplateHelper {

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplateHelper() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:testdb");
        dataSourceBuilder.username("SA");
        dataSourceBuilder.password("");
        DataSource dataSource=dataSourceBuilder.build();
        jdbcTemplate=new JdbcTemplate(dataSource);
    }

//    public void deleteChatProfileRelation(Long chatId) {
//        getJdbcTemplate().update("delete from chat_profile_relation where chat_id=?1", chatId);
//    }

    public List<Long> findProfileRelationsByChatId(Long chatId){
        return findRelationsById(chatId, "chat_profile_relation", "profile_id", "chat_id");
    }
    
    public List<Long> findChatRelationsByProfileId(Long profileId){
        return findRelationsById(profileId, "chat_profile_relation", "chat_id", "profile_id");
    }
    
    private List<Long> findRelationsById(Long id,String tableName,String targetColumnName,String findByColumnName){
        List<List<Long>> identificators=jdbcTemplate.query("select "+targetColumnName+" from "+tableName+" where "+findByColumnName+"=?1",new RowMapper<List<Long>>() {
            @Override
            public List<Long> mapRow(ResultSet rs, int arg1) throws SQLException {
                List<Long> results=new ArrayList<>();
                if (rs.isFirst())
                    results.add(rs.getLong(targetColumnName));
                while(rs.next())
                    results.add(rs.getLong(targetColumnName));
                return results;
            }
        } ,id);
        if (identificators.size()!=0)
            return identificators.get(0);
        else
            return new ArrayList<>();
    }
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
