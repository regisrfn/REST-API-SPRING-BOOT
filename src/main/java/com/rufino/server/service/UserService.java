package com.rufino.server.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.rufino.server.dao.UserDao;
import com.rufino.server.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserDao userDao;

    @Autowired
    public UserService(@Qualifier("DB_POSTGRES") UserDao userDao) {
        this.userDao = userDao;
    }

    public User addUser(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(hashedPassword);
        return userDao.insertUser(user);
    }

    public List<User> getAll(){
        return userDao.getAllUsers();
    }

    public User getUserById(UUID id){
        return userDao.getUser(id);
    }

    public Map<String, String> handleSqlError(DataIntegrityViolationException e) {
        String ss = e.getMessage();
        ss = ss.replace("\n", "").replace("\r", "");
        String pattern = ".*PreparedStatementCallback;.*SQL.*; ERROR:.*\"(\\w*user_\\w+)\".*";
        String error = (ss.replaceAll(pattern, "$1"));
        String[] errorString = error.split("_");
        Map<String, String> errors = new HashMap<>();

        if (errorString.length == 2) {
            error = "Invalid " + errorString[1] + " value";
            String fieldName = errorString[1].substring(0, 1).toUpperCase() + errorString[1].substring(1);
            errors.put(errorString[0] + fieldName, error);
        } else if ((errorString.length == 4)) {
            error = "Duplicated " + errorString[2];
            String fieldName = errorString[2].substring(0, 1).toUpperCase() + errorString[2].substring(1);
            errors.put(errorString[1] + fieldName, error);
        }

        return errors;
    }
}
