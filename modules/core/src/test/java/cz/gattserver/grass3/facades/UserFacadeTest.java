package cz.gattserver.grass3.facades;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.test.GrassFacadeTest;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class UserFacadeTest extends GrassFacadeTest {

	@Autowired
	private UserFacade userFacade;

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	@Test
	public void testActivateUser() {
		Long id = mockService.createMockUser(1);
		UserInfoDTO user = userFacade.getUser(id);
		assertFalse(user.isConfirmed());

		userFacade.activateUser(user.getId());
		user = userFacade.getUser(id);
		assertTrue(user.isConfirmed());
	}

	@Test
	public void testAddContentToFavourites() {
		Long userId = mockService.createMockUser(1);
		Long nodeId = mockService.createMockRootNode(2);

		String contentName = "testAddContentToFavouritesMockContentName";
		Long contentNodeId = contentNodeFacade.save("testAddContentToFavouritesMockModule", 34L, contentName, null,
				true, nodeId, userId, false, LocalDateTime.now(), null);

		Long user2Id = mockService.createMockUser(2);
		userFacade.addContentToFavourites(contentNodeId, user2Id);

		List<ContentNodeOverviewDTO> favourites = contentNodeFacade.getUserFavourite(user2Id, 0, 10);
		assertEquals(1, favourites.size());
		assertEquals(contentName, favourites.get(0).getName());
	}

	@Test
	public void testRegistrateNewUser() {
		String username = "TestUser";
		String email = "testuser@email.cz";
		userFacade.registrateNewUser(email, username, "testUser00012xxx$");

		UserInfoDTO user = userFacade.getUser(username);
		assertEquals(username, user.getName());
		assertEquals(email, user.getEmail());
		assertFalse(user.isConfirmed());
	}

}
