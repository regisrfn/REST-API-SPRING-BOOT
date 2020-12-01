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

@RestController
@RequestMapping("api/v1/user")
@CrossOrigin
public class UserController {

    private final UserService userService;
    private ObjectMapper om;
    

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        om = new ObjectMapper();
    }

    @PostMapping("register")
    public String saveUser(@RequestBody User user) {
        User userSaved;
        String message;
        JSONObject res = new JSONObject();
        try {
            userSaved = userService.addUser(user);
            message = "OK";
            String savedUserString = om.writeValueAsString(userSaved);
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create().withClaim("userId", user.getUserId().toString()).withClaim("userEmail", user.getUserEmail()).sign(algorithm);
            res.put("user", savedUserString);
            res.put("message", message);
            res.put("token", token);
            return res.toString();
        } catch (Exception e) {
            e.printStackTrace();
            message = "Not OK";
            res.put("message", message);
            return res.toString();
        }

    }

}
