package cz.gattserver.grass3.mock;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.gattserver.grass3.facades.ContentNodeFacade;
import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.facades.UserFacade;

@Service
public class MockService {

	@Autowired
	private UserFacade userFacade;

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	@Autowired
	private NodeFacade nodeFacade;

	public Long createMockUser() {
		return createMockUser(1);
	}

	public Long createMockUser(int variant) {
		Long id = userFacade.registrateNewUser("mockUser@mockUser.cz", "mockUserName" + variant, "mockUserPassword");
		return id;
	}

	public Long createMockRootNode() {
		Long id = nodeFacade.createNewNode(null, "mockNodeName");
		return id;
	}

	public Long createMockContent() {
		Long userId = createMockUser();
		Long nodeId = createMockRootNode();
		Long contentNodeId = contentNodeFacade.save("mockModule", 34L, "mockContentName", null, true, nodeId, userId,
				false, LocalDateTime.now(), null);
		return contentNodeId;
	}

}
