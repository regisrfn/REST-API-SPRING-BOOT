package com.rufino.server.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.rufino.server.dao.UserDao;
import com.rufino.server.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements UserDao {

    private JdbcTemplate jdbcTemplate;

    @Override
    public User insertUser(UUID id, User user) throws Exception {
        Date date = new Date();
        int result = jdbcTemplate.update(
                "INSERT INTO users " + "(user_id, user_name, user_nickname, user_email, user_password, created_at)"
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                id, user.getUserName(), user.getUserNickname(), user.getUserEmail(), user.getUserPassword(), date);
        if (result > 0) {
            user.setUserId(id);
            user.setCreatedAt(date);
            return user;
        }
        return null;        
    }

    @Override
    public int deleteUser(UUID id) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<User> getAllUsers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public User getUser(UUID id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public User updateUser(UUID id, User user) {
        // TODO Auto-generated method stub
        return null;
    }

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
