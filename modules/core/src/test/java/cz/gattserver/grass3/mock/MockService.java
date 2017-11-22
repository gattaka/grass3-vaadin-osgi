package cz.gattserver.grass3.mock;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.NodeService;
import cz.gattserver.grass3.services.UserService;
import cz.gattserver.grass3.test.MockUtils;

@Service
public class MockService {

	@Autowired
	private UserService userFacade;

	@Autowired
	private ContentNodeService contentNodeFacade;

	@Autowired
	private NodeService nodeFacade;

	public Long createMockUser(int variant) {
		Long id = userFacade.registrateNewUser(MockUtils.MOCK_USER_EMAIL + variant,
				MockUtils.MOCK_USER_NAME + variant, MockUtils.MOCK_USER_PASSWORD + variant);
		return id;
	}

	public Long createMockRootNode(int variant) {
		Long id = nodeFacade.createNewNode(null, MockUtils.MOCK_NODE_NAME + variant);
		return id;
	}

	public Long createMockContentNode(Long contentId, Set<String> tags, long nodeId, long userId, int variant) {
		Long contentNodeId = contentNodeFacade.save(MockUtils.MOCK_CONTENTNODE_MODULE + variant, contentId,
				MockUtils.MOCK_CONTENTNODE_NAME + variant, tags, true, nodeId, userId, false, LocalDateTime.now(),
				null);
		return contentNodeId;
	}

}
