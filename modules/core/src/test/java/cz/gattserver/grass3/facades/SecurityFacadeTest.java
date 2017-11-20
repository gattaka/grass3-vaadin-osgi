package cz.gattserver.grass3.facades;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.test.GrassFacadeTest;
import cz.gattserver.grass3.test.MockUtils;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class SecurityFacadeTest extends GrassFacadeTest {

	@Autowired
	private SecurityFacade securityFacade;

	@Autowired
	private UserFacade userFacade;

	@Test
	public void testLogin() {
		Long userId1 = mockService.createMockUser(1);
		userFacade.activateUser(userId1);
		securityFacade.login(MockUtils.MOCK_USER_NAME + 1, MockUtils.MOCK_USER_PASSWORD + 1, false);
		UserInfoTO user = securityFacade.getCurrentUser();
		assertEquals(MockUtils.MOCK_USER_NAME + 1, user.getUsername());
	}

}
