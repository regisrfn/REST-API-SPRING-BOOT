package com.rufino.server.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rufino.server.model.User;
import com.rufino.server.service.UserService;

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
    public UserController(UserService userService) {
        this.userService = userService;
        om = new ObjectMapper();
        dotenv = Dotenv.load();
        jwtSecret = dotenv.get("JWT_SECRET");
    }

    @PostMapping("register")
    public String saveUser(@RequestBody User user) {
        User userSaved;
        String message;
        String error;
        JSONObject res = new JSONObject();
        try {
            userSaved = userService.addUser(user);
            message = "OK";
            String savedUserString = om.writeValueAsString(userSaved);
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            String token = JWT.create().withClaim("userId", user.getUserId().toString())
                    .withClaim("userEmail", user.getUserEmail()).sign(algorithm);
            res.put("user", savedUserString);
            res.put("message", message);
            res.put("token", token);
            return res.toString();
        } catch (Exception e) {
            message = "Not OK";
            error = userService.handleSqlError(e);
            res.put("message", message);
            res.put("error", error);
            return res.toString();
        }

    }

}
