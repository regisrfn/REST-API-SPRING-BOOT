package com.rufino.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.rufino.server.model.User;
import com.rufino.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@AutoConfigureMockMvc
class ServerApplicationTests {

	@Autowired
	private UserService userService;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void clearTable() {
		jdbcTemplate.update("DELETE FROM USERS");
	}

	@Test
	void createNewUser() {
		try {
			User user = new User("Joe Doe", "joe@gmail.com", "123456");
			saveAndAssert(user);
			assert (true);
		} catch (Exception e) {
			String columnError = userService.handleSqlError(e);
			e.printStackTrace();
			assertEquals("user_name", columnError);
		}

	}

	@Test
	void createNewUser_error_nameExpected() {
		try {
			User user = new User(null, "regis@gmail.com", "123456");
			saveAndAssert(user);
			assert (false);
		} catch (Exception e) {
			e.printStackTrace();
			String columnError = userService.handleSqlError(e);
			assertEquals("Invalid name value", columnError);
		}

	}

	@Test
	void createNewUser_error_emailExpected() {
		try {
			User user = new User("Joe Doe", null, "123456");
			saveAndAssert(user);
			assert (false);
		} catch (Exception e) {
			e.printStackTrace();
			String columnError = userService.handleSqlError(e);
			assertEquals("Invalid email value", columnError);
		}
	}

	@Test
	void createNewUser_error_duplicatedEmail() {
		try {
			User user = new User("Joe Doe", "joe@gmail.com", "123456");
			saveAndAssert(user);
			User newUser = new User("John Doe", "joe@gmail.com", "123456");
			saveAndAssert(newUser,1,2);
			assert (false);
		} catch (Exception e) {
			e.printStackTrace();
			String columnError = userService.handleSqlError(e);
			assertEquals("Duplicated email", columnError);
		}
	}

	private void saveAndAssert(User user) throws Exception {
		long countBeforeInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
		assertEquals(0, countBeforeInsert);
		userService.addUser(user);
		long countAfterInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
		assertEquals(1, countAfterInsert);
	}

	private void saveAndAssert(User user, int before, int after) throws Exception {
		long countBeforeInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
		assertEquals(before, countBeforeInsert);
		userService.addUser(user);
		long countAfterInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
		assertEquals(after, countAfterInsert);
	}

}
