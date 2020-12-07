package com.rufino.server.repository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rufino.server.dao.UserDao;
import com.rufino.server.model.User;

import org.json.JSONObject;
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
        String sql = "UPDATE USERS SET ";
        ObjectMapper om = new ObjectMapper();
        try {
            String userString = om.writeValueAsString(user);
            JSONObject jsonObject = new JSONObject(userString);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                sql = sql + key.replaceAll("([A-Z])", "_$1").toLowerCase() + "='" + jsonObject.get(key) + "' ";
                if (keys.hasNext()) {
                    sql = sql + ", ";
                }
            }
            int result = jdbcTemplate.update(sql + "where user_id = ?", id);
            return (result > 0 ? getUser(id) : null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
