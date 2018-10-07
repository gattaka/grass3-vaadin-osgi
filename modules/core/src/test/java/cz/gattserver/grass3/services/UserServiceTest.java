package cz.gattserver.grass3.services;

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
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.UserService;
import cz.gattserver.grass3.test.AbstractDBUnitTest;
import cz.gattserver.grass3.test.MockUtils;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class UserServiceTest extends AbstractDBUnitTest {

	@Autowired
	private UserService userService;

	@Autowired
	private ContentNodeService contentNodeService;

	@Test
	public void testGetUserInfoFromAllUsers() {
		Long userId = coreMockService.createMockUser(1);
		coreMockService.createMockUser(2);
		List<UserInfoTO> list = userService.getUserInfoFromAllUsers();
		assertEquals(2, list.size());
		assertEquals(userId, list.get(0).getId());
		assertEquals(MockUtils.MOCK_USER_EMAIL + 1, list.get(0).getEmail());
		assertEquals(MockUtils.MOCK_USER_NAME + 1, list.get(0).getName());
		assertNotNull(list.get(0).getPassword());
		assertNotNull(list.get(0).getRegistrationDate());
		assertNull(list.get(0).getLastLoginDate());
		assertEquals(1, list.get(0).getRoles().size());
		assertTrue(list.get(0).getRoles().contains(CoreRole.USER));
	}

	@Test
	public void testChangeUserRoles() {
		Long userId = coreMockService.createMockUser(1);
		UserInfoTO user = userService.getUserById(userId);
		assertEquals(1, user.getRoles().size());
		assertTrue(user.getRoles().contains(CoreRole.USER));

		Set<CoreRole> roles = new HashSet<>();
		roles.add(CoreRole.ADMIN);
		roles.add(CoreRole.FRIEND);
		userService.changeUserRoles(userId, roles);

		user = userService.getUserById(userId);
		assertEquals(2, user.getRoles().size());
		assertTrue(user.getRoles().contains(CoreRole.ADMIN));
		assertTrue(user.getRoles().contains(CoreRole.FRIEND));
	}

	@Test(expected = NullPointerException.class)
	public void testChangeUserRoles_fail() {
		userService.changeUserRoles(1L, null);
	}

	@Test
	public void testBanUser() {
		Long userId = coreMockService.createMockUser(1);
		UserInfoTO user = userService.getUserById(userId);
		assertFalse(user.isConfirmed());

		userService.activateUser(userId);
		user = userService.getUserById(userId);
		assertTrue(user.isConfirmed());

		userService.banUser(userId);
		user = userService.getUserById(userId);
		assertFalse(user.isConfirmed());
	}

	@Test
	public void testActivateUser() {
		Long userId = coreMockService.createMockUser(1);
		UserInfoTO user = userService.getUserById(userId);
		assertFalse(user.isConfirmed());

		userService.activateUser(user.getId());
		user = userService.getUserById(userId);
		assertTrue(user.isConfirmed());
	}

	@Test
	public void testAddContentToFavourites() {
		Long userId = coreMockService.createMockUser(1);
		Long nodeId = coreMockService.createMockRootNode(2);
		Long contentNodeId = coreMockService.createMockContentNode(220L, null, nodeId, userId, 1);

		Long user2Id = coreMockService.createMockUser(2);
		userService.addContentToFavourites(contentNodeId, user2Id);

		List<ContentNodeOverviewTO> favourites = contentNodeService.getUserFavourite(user2Id, 0, 10);
		assertEquals(1, favourites.size());
		assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 1, favourites.get(0).getName());
	}

	@Test
	public void testHasInFavourite() {
		Long userId = coreMockService.createMockUser(1);
		Long nodeId = coreMockService.createMockRootNode(2);
		Long contentNodeId = coreMockService.createMockContentNode(220L, null, nodeId, userId, 1);
		Long contentNodeId2 = coreMockService.createMockContentNode(20L, null, nodeId, userId, 2);

		Long user2Id = coreMockService.createMockUser(2);
		userService.addContentToFavourites(contentNodeId, user2Id);

		assertTrue(userService.hasInFavourites(contentNodeId, user2Id));
		assertFalse(userService.hasInFavourites(contentNodeId2, user2Id));
	}

	@Test
	public void testRemoveContentFromFavourites_manual() {
		Long userId = coreMockService.createMockUser(1);
		Long nodeId = coreMockService.createMockRootNode(2);
		Long contentNodeId = coreMockService.createMockContentNode(220L, null, nodeId, userId, 1);

		Long user2Id = coreMockService.createMockUser(2);
		userService.addContentToFavourites(contentNodeId, user2Id);

		List<ContentNodeOverviewTO> favourites = contentNodeService.getUserFavourite(user2Id, 0, 10);
		assertEquals(1, favourites.size());
		assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 1, favourites.get(0).getName());

		userService.removeContentFromFavourites(contentNodeId, user2Id);

		favourites = contentNodeService.getUserFavourite(user2Id, 0, 10);
		assertTrue(favourites.isEmpty());
	}

	@Test
	public void testRemoveContentFromFavourites_byContentDelete() {
		Long userId = coreMockService.createMockUser(1);
		Long nodeId = coreMockService.createMockRootNode(2);
		Long contentNodeId = coreMockService.createMockContentNode(220L, null, nodeId, userId, 1);

		Long user2Id = coreMockService.createMockUser(2);
		userService.addContentToFavourites(contentNodeId, user2Id);

		List<ContentNodeOverviewTO> favourites = contentNodeService.getUserFavourite(user2Id, 0, 10);
		assertEquals(1, favourites.size());
		assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 1, favourites.get(0).getName());

		contentNodeService.deleteByContentNodeId(contentNodeId);

		favourites = contentNodeService.getUserFavourite(user2Id, 0, 10);
		assertTrue(favourites.isEmpty());
	}

	@Test
	public void testRegistrateNewUser() {
		String username = "TestUser";
		String email = "testuser@email.cz";
		userService.registrateNewUser(email, username, "testUser00012xxx$");

		UserInfoTO user = userService.getUser(username);
		assertEquals(username, user.getName());
		assertEquals(email, user.getEmail());
		assertFalse(user.isConfirmed());
	}

	@Test(expected = NullPointerException.class)
	public void testRegistrateNewUser_fail() {
		userService.registrateNewUser(null, "username", "testUser00012xxx$");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrateNewUser_fail2() {
		userService.registrateNewUser("", "username", "testUser00012xxx$");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrateNewUser_fail3() {
		userService.registrateNewUser(" ", "username", "testUser00012xxx$");
	}

	@Test(expected = NullPointerException.class)
	public void testRegistrateNewUser_fail4() {
		userService.registrateNewUser("email", null, "testUser00012xxx$");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrateNewUser_fail5() {
		userService.registrateNewUser("email", "", "testUser00012xxx$");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrateNewUser_fail6() {
		userService.registrateNewUser("email", " ", "testUser00012xxx$");
	}

	@Test(expected = NullPointerException.class)
	public void testRegistrateNewUser_fail7() {
		userService.registrateNewUser("email", "username", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrateNewUser_fail8() {
		userService.registrateNewUser("email", "username", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrateNewUser_fail9() {
		userService.registrateNewUser("email", "username", " ");
	}

	@Test(expected = NullPointerException.class)
	public void testGetUser_fail() {
		userService.getUser(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetUser_fail2() {
		userService.getUser("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetUser_fail3() {
		userService.getUser(" ");
	}

}
