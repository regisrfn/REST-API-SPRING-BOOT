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

    public String handleError(Exception e) {
        String ss = e.getMessage();
        ss = ss.replace("\n", "").replace("\r", "");
        String pattern = "(PreparedStatementCallback;.*SQL.*; ERROR:.*column \")(\\w+)(\".*)()";
        String update = (ss.replaceAll(pattern, "$2"));
        return update;
    }
}
