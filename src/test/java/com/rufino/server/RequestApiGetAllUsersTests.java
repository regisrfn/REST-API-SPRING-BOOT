package com.rufino.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rufino.server.model.User;
import com.rufino.server.service.UserService;

import org.json.JSONObject;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class RequestApiGetAllUsersTests {
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
    void getAllUsersTest() throws Exception {
        JSONObject my_obj = new JSONObject();
        ObjectMapper om = new ObjectMapper();

        MvcResult result = mockMvc
                .perform(get("/api/v1/user/").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        List<User> userList = Arrays.asList(om.readValue(result.getResponse().getContentAsString(), User[].class));

        assertThat(userList.size()).isEqualTo(0);

        List<User> usersList = userService.getAll();
        assertThat(usersList.size()).isEqualTo(0);
        User user = new User("Joe Doe", "joe@gmail.com", "123456");
        saveAndAssert(user, 0, 1);
        User newUser = new User("John Doe", "joe2@gmail.com", "123456");
        saveAndAssert(newUser, 1, 2);

        result = mockMvc
                .perform(get("/api/v1/user/").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        userList = Arrays.asList(om.readValue(result.getResponse().getContentAsString(), User[].class));

        assertThat(userList.size()).isEqualTo(2);

    }

    private void saveAndAssert(User user, int before, int after) {
        long countBeforeInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
        assertEquals(before, countBeforeInsert);
        userService.addUser(user);
        long countAfterInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
        assertEquals(after, countAfterInsert);
    }
}
