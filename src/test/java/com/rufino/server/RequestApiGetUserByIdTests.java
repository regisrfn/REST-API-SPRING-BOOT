package com.rufino.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rufino.server.model.User;
import com.rufino.server.service.UserService;

import org.hamcrest.core.Is;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class RequestApiGetUserByIdTests {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

    @BeforeEach
    void clearTable() {
        jdbcTemplate.update("DELETE FROM USERS");
    }

    @Test
    void getUser() throws Exception {
        User user = new User("Joe Doe", "joe@gmail.com", "123456");
        saveAndAssert(user, 0, 1);

        mockMvc.perform(get("/api/v1/user/" + user.getUserId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName", Is.is(user.getUserName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userEmail", Is.is(user.getUserEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userPassword", Is.is(user.getUserPassword())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Is.is(user.getUserId().toString())))

                .andExpect(status().isOk()).andReturn();

    }

    @Test
    void getEmptyUser() throws Exception {
        User user = new User("Joe Doe", "joe@gmail.com", "123456");
        saveAndAssert(user, 0, 1);
        mockMvc.perform(get("/api/v1/user/" + "8737659b-3b97-40c0-9529-f0741bba0eeb"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.apiError", Is.is("User not found")))
                .andExpect(status().isNotFound()).andReturn();

    }

    @Test
    void itShouldNotGetUser_UUID_error() throws Exception {
        User user = new User("Joe Doe", "joe@gmail.com", "123456");
        saveAndAssert(user, 0, 1);
        mockMvc.perform(get("/api/v1/user/" + "abc"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.apiError", Is.is("Invalid user UUID format")))
                .andExpect(status().isBadRequest()).andReturn();

    }

    private void saveAndAssert(User user, int before, int after) {
        long countBeforeInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
        assertEquals(before, countBeforeInsert);
        userService.addUser(user);
        long countAfterInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
        assertEquals(after, countAfterInsert);
    }
}
