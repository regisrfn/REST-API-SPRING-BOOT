package com.rufino.server.service;

import java.util.List;
import java.util.UUID;

import com.rufino.server.dao.UserDao;
import com.rufino.server.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public User updateUserById(UUID id, User user){
        return userDao.updateUser(id, user);
    }

    public int deleteUser(UUID id){
        return userDao.deleteUser(id);
    }
}
