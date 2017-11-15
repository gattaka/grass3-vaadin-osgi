package cz.gattserver.grass3.mock;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.gattserver.grass3.facades.ContentNodeFacade;
import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.facades.UserFacade;
import cz.gattserver.grass3.test.MockUtils;

@Service
public class MockService {

	@Autowired
	private UserFacade userFacade;

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	@Autowired
	private NodeFacade nodeFacade;

	public Long createMockUser(int variant) {
		Long id = userFacade.registrateNewUser(MockUtils.MOCK_USER_NAME + variant + "@mockUser.cz",
				MockUtils.MOCK_USER_NAME + variant, MockUtils.MOCK_USER_PASSWORD + variant);
		return id;
	}

	public Long createMockRootNode(int variant) {
		Long id = nodeFacade.createNewNode(null, MockUtils.MOCK_NODE_NAME + variant);
		return id;
	}

	public Long createMockContentNode(Long contentId, Set<String> tags, Long nodeId, Long userId, int variant) {
		Long contentNodeId = contentNodeFacade.save(MockUtils.MOCK_CONTENTNODE_MODULE + variant, contentId,
				MockUtils.MOCK_CONTENTNODE_NAME + variant, tags, true, nodeId, userId, false, LocalDateTime.now(),
				null);
		return contentNodeId;
	}

}
