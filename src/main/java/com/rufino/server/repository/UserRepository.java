package com.rufino.server.repository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.rufino.server.dao.UserDao;
import com.rufino.server.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("DB_POSTGRES")
public class UserRepository implements UserDao {

    private JdbcTemplate jdbcTemplate;
    private List<User> usersList;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        usersList = new ArrayList<>();
    }

    @Override
    public User insertUser(UUID id, User user) {
        user.setCreatedAt(ZonedDateTime.now(ZoneId.of("Z")).toString());
        user.setUserId(id);
        int result = jdbcTemplate.update(
                "INSERT INTO users " + "(user_id, user_name, user_nickname, user_email, user_password, created_at)"
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                user.getUserId(), user.getUserName(), user.getUserNickname(), user.getUserEmail(),
                user.getUserPassword(), user.getCreatedAt());
        return (result > 0 ? user : null);
    }

    @Override
    public int deleteUser(UUID id) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM USERS";
        usersList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<User>(User.class));
        return usersList;
    }

    @Override
    public User getUser(UUID id) {
        String sql = "SELECT * FROM USERS WHERE user_id = ?";
        usersList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<User>(User.class), new Object[] { id });
        ;
        if (usersList.isEmpty()) {
            return null;
        }
        return usersList.get(0);
    }

    @Override
    public User updateUser(UUID id, User user) {
        // TODO Auto-generated method stub
        return null;
    }

}
