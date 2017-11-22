package cz.gattserver.grass3.facades;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.test.GrassFacadeTest;
import cz.gattserver.grass3.test.MockUtils;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class UserFacadeTest extends GrassFacadeTest {

	@Autowired
	private UserFacade userFacade;

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	@Test
	public void testGetUserInfoFromAllUsers() {
		Long userId = mockService.createMockUser(1);
		mockService.createMockUser(2);
		List<UserInfoTO> list = userFacade.getUserInfoFromAllUsers();
		assertEquals(2, list.size());
		assertEquals(userId, list.get(0).getId());
		assertEquals(MockUtils.MOCK_USER_EMAIL + 1, list.get(0).getEmail());
		assertEquals(MockUtils.MOCK_USER_NAME + 1, list.get(0).getName());
		assertNotNull(list.get(0).getPassword());
		assertNotNull(list.get(0).getRegistrationDate());
		assertNull(list.get(0).getLastLoginDate());
		assertEquals(1, list.get(0).getRoles().size());
		assertTrue(list.get(0).getRoles().contains(Role.USER));
	}

	@Test
	public void testChangeUserRoles() {
		Long userId = mockService.createMockUser(1);
		UserInfoTO user = userFacade.getUserById(userId);
		assertEquals(1, user.getRoles().size());
		assertTrue(user.getRoles().contains(Role.USER));

		Set<Role> roles = new HashSet<>();
		roles.add(Role.ADMIN);
		roles.add(Role.FRIEND);
		userFacade.changeUserRoles(userId, roles);

		user = userFacade.getUserById(userId);
		assertEquals(2, user.getRoles().size());
		assertTrue(user.getRoles().contains(Role.ADMIN));
		assertTrue(user.getRoles().contains(Role.FRIEND));
	}

	@Test(expected = NullPointerException.class)
	public void testChangeUserRoles_fail() {
		userFacade.changeUserRoles(1L, null);
	}

	@Test
	public void testBanUser() {
		Long userId = mockService.createMockUser(1);
		UserInfoTO user = userFacade.getUserById(userId);
		assertFalse(user.isConfirmed());

		userFacade.activateUser(userId);
		user = userFacade.getUserById(userId);
		assertTrue(user.isConfirmed());

		userFacade.banUser(userId);
		user = userFacade.getUserById(userId);
		assertFalse(user.isConfirmed());
	}

	@Test
	public void testActivateUser() {
		Long userId = mockService.createMockUser(1);
		UserInfoTO user = userFacade.getUserById(userId);
		assertFalse(user.isConfirmed());

		userFacade.activateUser(user.getId());
		user = userFacade.getUserById(userId);
		assertTrue(user.isConfirmed());
	}

	@Test
	public void testAddContentToFavourites() {
		Long userId = mockService.createMockUser(1);
		Long nodeId = mockService.createMockRootNode(2);
		Long contentNodeId = mockService.createMockContentNode(220L, null, nodeId, userId, 1);

		Long user2Id = mockService.createMockUser(2);
		userFacade.addContentToFavourites(contentNodeId, user2Id);

		List<ContentNodeOverviewTO> favourites = contentNodeFacade.getUserFavourite(user2Id, 0, 10);
		assertEquals(1, favourites.size());
		assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 1, favourites.get(0).getName());
	}

	@Test
	public void testHasInFavourite() {
		Long userId = mockService.createMockUser(1);
		Long nodeId = mockService.createMockRootNode(2);
		Long contentNodeId = mockService.createMockContentNode(220L, null, nodeId, userId, 1);
		Long contentNodeId2 = mockService.createMockContentNode(20L, null, nodeId, userId, 2);

		Long user2Id = mockService.createMockUser(2);
		userFacade.addContentToFavourites(contentNodeId, user2Id);

		assertTrue(userFacade.hasInFavourites(contentNodeId, user2Id));
		assertFalse(userFacade.hasInFavourites(contentNodeId2, user2Id));
	}

	@Test
	public void testRemoveContentFromFavourites_manual() {
		Long userId = mockService.createMockUser(1);
		Long nodeId = mockService.createMockRootNode(2);
		Long contentNodeId = mockService.createMockContentNode(220L, null, nodeId, userId, 1);

		Long user2Id = mockService.createMockUser(2);
		userFacade.addContentToFavourites(contentNodeId, user2Id);

		List<ContentNodeOverviewTO> favourites = contentNodeFacade.getUserFavourite(user2Id, 0, 10);
		assertEquals(1, favourites.size());
		assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 1, favourites.get(0).getName());

		userFacade.removeContentFromFavourites(contentNodeId, user2Id);

		favourites = contentNodeFacade.getUserFavourite(user2Id, 0, 10);
		assertTrue(favourites.isEmpty());
	}

	@Test
	public void testRemoveContentFromFavourites_byContentDelete() {
		Long userId = mockService.createMockUser(1);
		Long nodeId = mockService.createMockRootNode(2);
		Long contentNodeId = mockService.createMockContentNode(220L, null, nodeId, userId, 1);

		Long user2Id = mockService.createMockUser(2);
		userFacade.addContentToFavourites(contentNodeId, user2Id);

		List<ContentNodeOverviewTO> favourites = contentNodeFacade.getUserFavourite(user2Id, 0, 10);
		assertEquals(1, favourites.size());
		assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 1, favourites.get(0).getName());

		contentNodeFacade.deleteByContentNodeId(contentNodeId);

		favourites = contentNodeFacade.getUserFavourite(user2Id, 0, 10);
		assertTrue(favourites.isEmpty());
	}

	@Test
	public void testRegistrateNewUser() {
		String username = "TestUser";
		String email = "testuser@email.cz";
		userFacade.registrateNewUser(email, username, "testUser00012xxx$");

		UserInfoTO user = userFacade.getUser(username);
		assertEquals(username, user.getName());
		assertEquals(email, user.getEmail());
		assertFalse(user.isConfirmed());
	}

	@Test(expected = NullPointerException.class)
	public void testRegistrateNewUser_fail() {
		userFacade.registrateNewUser(null, "username", "testUser00012xxx$");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrateNewUser_fail2() {
		userFacade.registrateNewUser("", "username", "testUser00012xxx$");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrateNewUser_fail3() {
		userFacade.registrateNewUser(" ", "username", "testUser00012xxx$");
	}

	@Test(expected = NullPointerException.class)
	public void testRegistrateNewUser_fail4() {
		userFacade.registrateNewUser("email", null, "testUser00012xxx$");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrateNewUser_fail5() {
		userFacade.registrateNewUser("email", "", "testUser00012xxx$");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrateNewUser_fail6() {
		userFacade.registrateNewUser("email", " ", "testUser00012xxx$");
	}

	@Test(expected = NullPointerException.class)
	public void testRegistrateNewUser_fail7() {
		userFacade.registrateNewUser("email", "username", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrateNewUser_fail8() {
		userFacade.registrateNewUser("email", "username", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrateNewUser_fail9() {
		userFacade.registrateNewUser("email", "username", " ");
	}

	@Test(expected = NullPointerException.class)
	public void testGetUser_fail() {
		userFacade.getUser(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetUser_fail2() {
		userFacade.getUser("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetUser_fail3() {
		userFacade.getUser(" ");
	}

}
