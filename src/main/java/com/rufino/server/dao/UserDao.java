package com.rufino.server.dao;

import java.util.List;
import java.util.UUID;

import com.rufino.server.model.User;

public interface UserDao {
    User insertUser(UUID id, User user) throws Exception;
    int deleteUser(UUID id);
    List<User> getAllUsers();
    User getUser(UUID id);
    User updateUser(UUID id, User user);

    default User insertUser(User user) throws Exception {
        UUID id = UUID.randomUUID();
        return insertUser(id, user);
    }
}
