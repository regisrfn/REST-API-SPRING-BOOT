package com.rufino.server.service;

import com.rufino.server.dao.UserDao;
import com.rufino.server.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User addUser(User user) throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(hashedPassword);
        return userDao.insertUser(user);
    }

    public String handleSqlError(Exception e) {
        String ss = e.getMessage();
        ss = ss.replace("\n", "").replace("\r", "");
        String pattern = ".*PreparedStatementCallback;.*SQL.*; ERROR:.*\"(\\w*user_\\w+)\".*";
        String error = (ss.replaceAll(pattern, "$1"));

        String[] errorString = error.split("_");

        if (errorString.length == 2) {
            error = "Invalid " + errorString[1] + " value";
        } else if ((errorString.length == 4)) {
            error = "Duplicated " + errorString[2];
        }

        return error;
    }
}
