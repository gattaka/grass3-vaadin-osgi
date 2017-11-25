package cz.gattserver.grass3.services;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.services.UserService;
import cz.gattserver.grass3.services.impl.LoginResult;
import cz.gattserver.grass3.test.AbstractDBUnitTest;
import cz.gattserver.grass3.test.MockUtils;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
@WebAppConfiguration
public class SecurityServiceTest extends AbstractDBUnitTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private Filter springSecurityFilterChain;

	private MockMvc mvc;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private UserService userService;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();
	}

	@Test
	public void testLogin() throws Exception {
		MvcResult mvcResult = mvc.perform(get("/")).andReturn();

		Long userId1 = coreMockService.createMockUser(1);
		userService.activateUser(userId1);
		LoginResult result = securityService.login(MockUtils.MOCK_USER_NAME + 1, MockUtils.MOCK_USER_PASSWORD + 1,
				false, mvcResult.getRequest(), mvcResult.getResponse());
		assertEquals(LoginResult.SUCCESS, result);
		UserInfoTO user = securityService.getCurrentUser();
		assertEquals(MockUtils.MOCK_USER_NAME + 1, user.getUsername());
	}

	@Test
	public void testLogin_remember() throws Exception {
		MvcResult mvcResult = mvc.perform(get("/")).andReturn();

		Long userId1 = coreMockService.createMockUser(1);
		userService.activateUser(userId1);
		LoginResult result = securityService.login(MockUtils.MOCK_USER_NAME + 1, MockUtils.MOCK_USER_PASSWORD + 1, true,
				mvcResult.getRequest(), mvcResult.getResponse());
		assertEquals(LoginResult.SUCCESS, result);
		UserInfoTO user = securityService.getCurrentUser();
		assertEquals(MockUtils.MOCK_USER_NAME + 1, user.getUsername());
	}

	@Test
	public void testLogin_failed() throws Exception {
		MvcResult mvcResult = mvc.perform(get("/")).andReturn();

		Long userId1 = coreMockService.createMockUser(1);
		userService.activateUser(userId1);
		LoginResult result = securityService.login("wrong", MockUtils.MOCK_USER_PASSWORD + 1, false,
				mvcResult.getRequest(), mvcResult.getResponse());
		assertEquals(LoginResult.FAILED_CREDENTIALS, result);
	}

	@Test
	public void testLogin_failed2() throws Exception {
		MvcResult mvcResult = mvc.perform(get("/")).andReturn();

		Long userId1 = coreMockService.createMockUser(1);
		userService.activateUser(userId1);
		LoginResult result = securityService.login(MockUtils.MOCK_USER_NAME + 1, "wrong", false, mvcResult.getRequest(),
				mvcResult.getResponse());
		assertEquals(LoginResult.FAILED_CREDENTIALS, result);
	}

	@Test
	public void testGetCurrentUser() {
		UserInfoTO user = securityService.getCurrentUser();
		assertNotNull(user);
		assertNull(user.getName());
	}

}
