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
public class CoreMockService {

	@Autowired
	private UserService userService;

	@Autowired
	private ContentNodeService contentNodeService;

	@Autowired
	private NodeService nodeService;

	public long createMockUser(int variant) {
		long userId = userService.registrateNewUser(MockUtils.MOCK_USER_EMAIL + variant,
				MockUtils.MOCK_USER_NAME + variant, MockUtils.MOCK_USER_PASSWORD + variant);
		userService.activateUser(userId);
		return userId;
	}

	public long createMockRootNode(int variant) {
		long id = nodeService.createNewNode(null, MockUtils.MOCK_NODE_NAME + variant);
		return id;
	}

	public long createMockContentNode(Long contentId, Set<String> tags, long nodeId, long userId, int variant) {
		long contentNodeId = contentNodeService.save(MockUtils.MOCK_CONTENTNODE_MODULE + variant, contentId,
				MockUtils.MOCK_CONTENTNODE_NAME + variant, tags, true, nodeId, userId, false, LocalDateTime.now(),
				null);
		return contentNodeId;
	}

}
