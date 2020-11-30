package com.rufino.server.api;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String home(){
        JSONObject resp = new JSONObject();
        resp.put("message", "Hellow World from Spring Boot");
        return  resp.toString();
    }    
}
