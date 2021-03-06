package com.rufino.server.model;

import java.util.UUID;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "userId", "userName", "userNickname", "userEmail", "userPassword", "createdAt" })
public class User {
    private UUID userId;

    @NotBlank(message = "Value should not be empty")
    @Size(min = 3, max = 30, message = "Value must be between 3 and 30 characters")
    private String userName;

    private String userNickname;

    @NotBlank(message = "Value should not be empty")
    private String userEmail;

    @NotBlank(message = "Value should not be empty")
    @Min(value = 6, message = "Value must be greater then 6 characters")
    private String userPassword;
    
    private String createdAt;

    public UUID getUserId() {
        return userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public User(String userName, String userNickname, String userEmail, String userPassword) {
        this.userName = userName;
        this.userNickname = userNickname;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    public User(String userName, String userEmail, String userPassword) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    public User() {
    };
}
