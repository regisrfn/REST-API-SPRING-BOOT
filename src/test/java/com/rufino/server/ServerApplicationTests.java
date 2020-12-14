package com.rufino.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.rufino.server.exception.ApiHandlerException;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.model.User;
import com.rufino.server.service.UserService;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@AutoConfigureMockMvc
class ServerApplicationTests {

	@Autowired
	private UserService userService;
	@Autowired
	private ApiHandlerException apiHandlerException;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void clearTable() {
		jdbcTemplate.update("DELETE FROM USERS");
	}

	//////////////////////////////// CREATE USER /////////////////////////////////
	@Test
	void createNewUser() {
		try {
			User user = new User("Joe Doe", "joe@gmail.com", "123456");
			saveAndAssert(user);
			assert (true);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			assert (false);
		}

	}

	@Test
	void createNewUser_error_nameExpected() {
		try {
			User user = new User(null, "regis@gmail.com", "123456");
			saveAndAssert(user);
			assert (false);
		} catch (DataIntegrityViolationException e) {
			Map<String, String> columnError = apiHandlerException.handleSqlError(e);
			assertEquals("Value should not be empty", columnError.get("userName"));
		}

	}

	@Test
	void createNewUser_error_emailExpected() {
		try {
			User user = new User("Joe Doe", null, "123456");
			saveAndAssert(user);
			assert (false);
		} catch (DataIntegrityViolationException e) {
			Map<String, String> columnError = apiHandlerException.handleSqlError(e);
			assertEquals("Value should not be empty", columnError.get("userEmail"));
		}
	}

	@Test
	void createNewUser_error_duplicatedEmail() {
		try {
			User user = new User("Joe Doe", "joe@gmail.com", "123456");
			saveAndAssert(user);
			User newUser = new User("John Doe", "joe@gmail.com", "123456");
			saveAndAssert(newUser, 1, 2);
			assert (false);
		} catch (DuplicateKeyException e) {
			Map<String, String> columnError = apiHandlerException.handleSqlError(e);
			assertEquals("Duplicated email", columnError.get("userEmail"));
		}
	}

	//////////////////////////////// GET ALL USERS /////////////////////////////////
	@Test
	void itShouldGetAllUsers() {
		try {
			List<User> usersList = userService.getAll();
			assertThat(usersList.size()).isEqualTo(0);
			User user = new User("Joe Doe", "joe@gmail.com", "123456");
			saveAndAssert(user);
			User newUser = new User("John Doe", "joe2@gmail.com", "123456");
			saveAndAssert(newUser, 1, 2);

			usersList = userService.getAll();
			assertThat(usersList.size()).isEqualTo(2);
		} catch (DuplicateKeyException e) {
			Map<String, String> columnError = apiHandlerException.handleSqlError(e);
			assertEquals("Duplicated email", columnError.get("userEmail"));
		}
	}

	//////////////////////////////// GET USER BY ID
	//////////////////////////////// /////////////////////////////////
	@Test
	void itShouldGetUserById() {
		User user = new User("Joe Doe", "joe@gmail.com", "123456");
		saveAndAssert(user);
		User newUser = new User("Joe Doe", "joe2@gmail.com", "654321");
		saveAndAssert(newUser, 1, 2);

		User userFromDb = userService.getUserById(user.getUserId());
		assertThat(userFromDb.getUserEmail()).isEqualTo(user.getUserEmail());
		assertThat(userFromDb.getUserName()).isEqualTo(user.getUserName());
		assertThat(userFromDb.getUserPassword()).isEqualTo(user.getUserPassword());

		userFromDb = userService.getUserById(newUser.getUserId());
		assertThat(userFromDb.getUserEmail()).isEqualTo(newUser.getUserEmail());
		assertThat(userFromDb.getUserName()).isEqualTo(newUser.getUserName());
		assertThat(userFromDb.getUserPassword()).isEqualTo(newUser.getUserPassword());

	}

	@Test
	void itShouldGetNullUser() {
		User userFromDb = userService.getUserById(UUID.fromString("2755caca-e765-456c-ac2f-422602bd188c"));
		assertThat(userFromDb).isNull();
	}

	//////////////////////////////// UPDATE USER BY ID
	//////////////////////////////// /////////////////////////////////
	@Test
	void itShouldUpdateUserName() {
		User user = new User("Joe Doe", "joe@gmail.com", "123456");
		saveAndAssert(user);

		User updatedUser = new User();
		updatedUser.setUserName("Jonh Doe");
		User userFromDb = userService.updateUserById(user.getUserId(), updatedUser);

		assertThat(userFromDb.getUserName()).isEqualTo(updatedUser.getUserName());
		assertThat(userFromDb.getUserEmail()).isEqualTo(user.getUserEmail());
		assertThat(userFromDb.getCreatedAt()).isEqualTo(user.getCreatedAt());
		assertThat(userFromDb.getUserId()).isEqualTo(user.getUserId());
		assertThat(userFromDb.getUserPassword()).isEqualTo(user.getUserPassword());
		assertThat(userFromDb.getUserNickname()).isEqualTo(user.getUserNickname());

	}

	@Test
	void itShouldUpdateUserPassword() {
		User user = new User("Joe Doe", "joe@gmail.com", "123456");
		saveAndAssert(user);

		User updatedUser = new User();
		updatedUser.setUserPassword("654321");
		User userFromDb = userService.updateUserById(user.getUserId(), updatedUser);

		assertThat(userFromDb.getUserName()).isEqualTo(user.getUserName());
		assertThat(userFromDb.getUserEmail()).isEqualTo(user.getUserEmail());
		assertThat(userFromDb.getCreatedAt()).isEqualTo(user.getCreatedAt());
		assertThat(userFromDb.getUserId()).isEqualTo(user.getUserId());
		assertThat(userFromDb.getUserPassword()).isEqualTo(updatedUser.getUserPassword());
		assertThat(userFromDb.getUserNickname()).isEqualTo(user.getUserNickname());

	}

	@Test
	void itShouldUpdateUserEmail() {
		User user = new User("Joe Doe", "joe@gmail.com", "123456");
		saveAndAssert(user);

		User updatedUser = new User();
		updatedUser.setUserName("Jonh Doe");
		updatedUser.setUserEmail("john@gmail.com");
		User userFromDb = userService.updateUserById(user.getUserId(), updatedUser);

		assertThat(userFromDb.getUserName()).isEqualTo(updatedUser.getUserName());
		assertThat(userFromDb.getUserEmail()).isEqualTo(updatedUser.getUserEmail());
		assertThat(userFromDb.getCreatedAt()).isEqualTo(user.getCreatedAt());
		assertThat(userFromDb.getUserId()).isEqualTo(user.getUserId());
		assertThat(userFromDb.getUserPassword()).isEqualTo(user.getUserPassword());
		assertThat(userFromDb.getUserNickname()).isEqualTo(user.getUserNickname());
	}

	@Test
	void itShouldNotUpdateUser() {
		try {
			User user = new User("Joe Doe", "joe@gmail.com", "123456");
			saveAndAssert(user);
			User updatedUser = new User();
			updatedUser.setUserEmail(null);
			userService.updateUserById(user.getUserId(), updatedUser);

		} catch (ApiRequestException e) {
			assertEquals(e.getMessage(), "No valid data to update");
		}
	}

	//////////////////////////////// DELETE USER BY ID
	//////////////////////////////// /////////////////////////////////
	@Test
	public void itShouldDeleteUser() {
		User user = new User("Joe Doe", "joe@gmail.com", "123456");
		saveAndAssert(user);

		int result = userService.deleteUser(user.getUserId());
		long countAfterInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
		assertEquals(0, countAfterInsert);
		if (result > 0) {
			assert (true);
		} else {
			assert (false);
		}
	}

	@Test
	public void itShouldNotDeleteUser() {
		User user = new User("Joe Doe", "joe@gmail.com", "123456");
		saveAndAssert(user);

		int result = userService.deleteUser(UUID.fromString("2755caca-e765-456c-ac2f-422602bd188c"));
		long countAfterInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
		assertEquals(1, countAfterInsert);
		if (result > 0) {
			assert (false);
		} else {
			assert (true);
		}
	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void saveAndAssert(User user) {
		long countBeforeInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
		assertEquals(0, countBeforeInsert);
		userService.addUser(user);
		long countAfterInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
		assertEquals(1, countAfterInsert);
	}

	private void saveAndAssert(User user, int before, int after) {
		long countBeforeInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
		assertEquals(before, countBeforeInsert);
		userService.addUser(user);
		long countAfterInsert = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
		assertEquals(after, countAfterInsert);
	}

}
