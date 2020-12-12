package com.rufino.server.service;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rufino.server.dao.UserDao;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.model.User;
import com.rufino.server.validation.ValidateEmail;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserDao userDao;
    private ObjectMapper om;
    @Autowired
    private ValidateEmail validateEmail;

    @Autowired
    public UserService(@Qualifier("DB_POSTGRES") UserDao userDao, ObjectMapper om) {
        this.om = om;
        this.userDao = userDao;
    }

    public User addUser(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(hashedPassword);
        return userDao.insertUser(user);
    }

    public List<User> getAll() {
        return userDao.getAllUsers();
    }

    public User getUserById(UUID id) {
        return userDao.getUser(id);
    }

    public User updateUserById(UUID id, User user) {
        String userString;
        try {
            userString = om.writeValueAsString(user);
            JSONObject jsonObject = new JSONObject(userString);
            Iterator<String> keys = jsonObject.keys();
            if (!keys.hasNext()) {
                throw new ApiRequestException("No valid data to update");
            }
            while (keys.hasNext()) {
                String key = keys.next();
                switch (key) {
                    case "userEmail":
                        if (!validateEmail.test(jsonObject.get(key).toString()))
                            throw new ApiRequestException("Invalid email format");
                        break;
                    default:
                        break;
                }
            }
            return userDao.updateUser(id, user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ApiRequestException(e.getMessage());
        }
    }

    public int deleteUser(UUID id) {
        return userDao.deleteUser(id);
    }
}
