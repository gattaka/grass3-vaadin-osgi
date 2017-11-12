package cz.gattserver.grass3.facades;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.test.GrassFacadeTest;

public class UserFacadeTest extends GrassFacadeTest {

	@Autowired
	private UserFacade userFacade;

	@Test
	public void test() {
		String username = "TestUser";
		String email = "testuser@email.cz";
		userFacade.registrateNewUser(email, username, "testUser00012xxx$");

		UserInfoDTO user = userFacade.getUser(username);
		assertEquals(username, user.getName());
		assertEquals(email, user.getEmail());
	}

}
