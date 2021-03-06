package com.rufino.server;

import org.hamcrest.core.Is;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.github.cdimascio.dotenv.Dotenv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rufino.server.model.User;

@SpringBootTest
@AutoConfigureMockMvc
public class PostUserHttpTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;

    Dotenv dotenv = Dotenv.load();
    private String jwtSecret = dotenv.get("JWT_SECRET");

    @BeforeEach
    void clearTable() {
        jdbcTemplate.update("DELETE FROM USERS");
    }

    @Test
    void addUserTest() throws Exception {
        JSONObject my_obj = new JSONObject();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        ObjectMapper om = new ObjectMapper();
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);

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

        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(res.getString("token"));
    }

    @Test
    void addUserTest_errorExpected_name() throws Exception {
        JSONObject my_obj = new JSONObject();
        my_obj.put("userNickname", "doe");
        my_obj.put("userEmail", "doe@gmail.com");
        my_obj.put("userPassword", "123456");

        mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.userName", Is.is("Value should not be empty")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void addUserTest_errorExpected_name2() throws Exception {
        JSONObject my_obj = new JSONObject();
        my_obj.put("userName", "   ");
        my_obj.put("userNickname", "doe");
        my_obj.put("userEmail", "doe@gmail.com");
        my_obj.put("userPassword", "123456");

        mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.userName", Is.is("Value should not be empty")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void addUserTest_errorExpected_nameTooShort() throws Exception {
        JSONObject my_obj = new JSONObject();
        my_obj.put("userName", "a");
        my_obj.put("userNickname", "doe");
        my_obj.put("userEmail", "doe@gmail.com");
        my_obj.put("userPassword", "123456");

        mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.userName",
                        Is.is("Value must be between 3 and 30 characters")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void addUserTest_errorExpected_nameTooBig() throws Exception {
        JSONObject my_obj = new JSONObject();
        my_obj.put("userName", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        my_obj.put("userNickname", "doe");
        my_obj.put("userEmail", "doe@gmail.com");
        my_obj.put("userPassword", "123456");

        mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.userName",
                        Is.is("Value must be between 3 and 30 characters")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void addUserTest_errorExpected_email() throws Exception {
        JSONObject my_obj = new JSONObject();
        my_obj.put("userName", "Joe Doe");
        my_obj.put("userNickname", "doe");
        my_obj.put("userPassword", "123456");

        mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.userEmail", Is.is("Value should not be empty")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    void addUserTest_errorExpected_emailNotValid() throws Exception {
        JSONObject my_obj = new JSONObject();
        my_obj.put("userName", "Joe Doe");
        my_obj.put("userNickname", "doe");
        my_obj.put("userPassword", "123456");
        my_obj.put("userEmail", "doegmail.com");

        mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.apiError", Is.is("Invalid email format")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(status().isBadRequest()).andReturn();

        my_obj.put("userEmail", "doe@gmail.com/");

        mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.apiError", Is.is("Invalid email format")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    void addUserTest_errorExpected_emailDuplicated() throws Exception {
        JSONObject my_obj = new JSONObject();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        ObjectMapper om = new ObjectMapper();
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);

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

        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(res.getString("token"));

        mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.userEmail", Is.is("Duplicated email")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(status().isBadRequest()).andReturn();

    }
}
