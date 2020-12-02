package com.rufino.server;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rufino.server.model.User;

@SpringBootTest
@AutoConfigureMockMvc
public class PostUserHttpTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void clearTable() {
        jdbcTemplate.update("DELETE FROM USERS");
    }

    @Test
    void addUserTest() throws Exception {
        JSONObject my_obj = new JSONObject();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        ObjectMapper om = new ObjectMapper();

        my_obj.put("userName", "Joe Doe");
        my_obj.put("userNickname", "doe");
        my_obj.put("userEmail", "doe@gmail.com");
        my_obj.put("userPassword", "123456");

        MvcResult result = mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        JSONObject res = new JSONObject(result.getResponse().getContentAsString());
        assertEquals("OK", res.getString("message"));
        assertNotNull(res.getString("user"));

        User savedUser = om.readValue(res.getString("user"), User.class);
        String hashedPassword = savedUser.getUserPassword();
        assert (passwordEncoder.matches("123456", hashedPassword));
    }

    @Test
    void addUserTest_errorExpected() throws Exception {
        JSONObject my_obj = new JSONObject();
        my_obj.put("userNickname", "doe");
        my_obj.put("userEmail", "doe@gmail.com");
        my_obj.put("userPassword", "123456");

        MvcResult result = mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        JSONObject res = new JSONObject(result.getResponse().getContentAsString());
        assertEquals("Not OK", res.getString("message"));
        assertEquals("Invalid name value", res.getString("error"));
    }

}
