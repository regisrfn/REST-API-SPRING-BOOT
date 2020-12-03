package com.rufino.server;

import com.rufino.server.validation.ValidateEmail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class EmailValidationTests {

    @Autowired
    private ValidateEmail validateEmail;

    @Test
    public void itShouldValidateEmail() {
        assertThat(validateEmail.test("test@gmail.com")).isTrue();
    }
    @Test
    public void itShouldNotValidateEmail() {
        assertThat(validateEmail.test("testgmail.com")).isFalse();
        assertThat(validateEmail.test("testgmailcom")).isFalse();
        assertThat(validateEmail.test("test@gmailcom")).isFalse();
    }

    
}
