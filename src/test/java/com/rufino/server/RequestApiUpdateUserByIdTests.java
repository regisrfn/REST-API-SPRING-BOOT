package com.rufino.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import net.minidev.json.JSONObject;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rufino.server.model.User;
import com.rufino.server.service.UserService;

import org.hamcrest.core.Is;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc

public class RequestApiUpdateUserByIdTests {

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
    void updateUserTest() throws Exception {
        User user = new User("Joe Doe", "joe@gmail.com", "123456");
        saveAndAssert(user, 0, 1);

        JSONObject my_obj = new JSONObject();
        my_obj.put("userName", "John Doe");

        mockMvc.perform(put("/api/v1/user/" + user.getUserId()).contentType(MediaType.APPLICATION_JSON)
                .content(my_obj.toString())).andExpect(MockMvcResultMatchers.jsonPath("$.userName", Is.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userEmail", Is.is(user.getUserEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName", Is.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userPassword", Is.is(user.getUserPassword())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Is.is(user.getCreatedAt())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Is.is(user.getUserId().toString())))
                .andExpect(status().isOk()).andReturn();

    }

    @Test
    void itShouldNotUpdateUser_BlankEmail() throws Exception {
        User user = new User("Joe Doe", "joe@gmail.com", "123456");
        saveAndAssert(user, 0, 1);

        JSONObject my_obj = new JSONObject();
        my_obj.put("userName", "John Doe");
        my_obj.put("userEmail", "  ");

        mockMvc.perform(put("/api/v1/user/" + user.getUserId()).contentType(MediaType.APPLICATION_JSON)
                .content(my_obj.toString())).andExpect(MockMvcResultMatchers.jsonPath("$.userName", Is.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userEmail", Is.is(user.getUserEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName", Is.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userPassword", Is.is(user.getUserPassword())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Is.is(user.getCreatedAt())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Is.is(user.getUserId().toString())))
                .andExpect(status().isOk()).andReturn();

    }

    @Test
    void itShouldNotUpdateUser_InvalidEmail() throws Exception {
        User user = new User("Joe Doe", "joe@gmail.com", "123456");
        saveAndAssert(user, 0, 1);

        JSONObject my_obj = new JSONObject();
        my_obj.put("userName", "John Doe");
        my_obj.put("userEmail", "joe@gmailcom");

        mockMvc.perform(put("/api/v1/user/" + user.getUserId()).contentType(MediaType.APPLICATION_JSON)
                .content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.apiError", Is.is("Invalid email format")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
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
