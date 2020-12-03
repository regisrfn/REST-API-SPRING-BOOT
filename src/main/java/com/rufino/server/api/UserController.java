package com.rufino.server.api;

import javax.validation.Valid;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.model.User;
import com.rufino.server.service.UserService;
import com.rufino.server.validation.ValidateEmail;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.cdimascio.dotenv.Dotenv;

@RestController
@RequestMapping("api/v1/user")
@CrossOrigin
public class UserController {

    private final UserService userService;
    private ObjectMapper om;
    Dotenv dotenv;
    String jwtSecret;

    @Autowired
    ValidateEmail validateEmail;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        om = new ObjectMapper();
        dotenv = Dotenv.load();
        jwtSecret = dotenv.get("JWT_SECRET");
    }

    @PostMapping("register")
    public String saveUser(@Valid @RequestBody User user) {

        JSONObject res = new JSONObject();
        try {
            if (!validateEmail.test(user.getUserEmail()))
                throw new ApiRequestException("Invalid email format");
            return save(user, res);
        } catch (JSONException | JsonProcessingException e) {
            throw new ApiRequestException(e.getMessage());
        }

    }

    private String save(User user, JSONObject res) throws JsonProcessingException, JSONException {
        User userSaved = userService.addUser(user);
        String savedUserString = om.writeValueAsString(userSaved);
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        String token = JWT.create().withClaim("userId", user.getUserId().toString())
                .withClaim("userEmail", user.getUserEmail()).sign(algorithm);
        res.put("user", savedUserString);
        res.put("message", "OK");
        res.put("token", token);
        return res.toString();
    }

}
