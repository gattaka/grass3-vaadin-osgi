package cz.gattserver.grass3.services.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryResults;

import cz.gattserver.grass3.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass3.interfaces.ContentNodeTO;
import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.domain.Node;
import cz.gattserver.grass3.model.domain.User;
import cz.gattserver.grass3.model.repositories.ContentNodeRepository;
import cz.gattserver.grass3.model.repositories.UserRepository;
import cz.gattserver.grass3.model.util.QuerydslUtil;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.services.CoreMapperService;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.services.UserService;

@Transactional
@Service
public class ContentNodeServiceImpl implements ContentNodeService {

	@Autowired
	private CoreMapperService mapper;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private ContentTagService contentTagService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContentNodeRepository contentNodeRepository;

	@Override
	public long save(String contentModuleId, long contentId, String name, Collection<String> tags, boolean publicated,
			long nodeId, long authorId, boolean draft, LocalDateTime date, Long draftSourceId) {
		Validate.notNull(contentModuleId, "'contentModuleId' nesmí být null");
		Validate.notNull(name, "'name' nesmí být null");

		if (date == null)
			date = LocalDateTime.now();

		ContentNode contentNode = new ContentNode();
		contentNode.setContentId(contentId);
		contentNode.setContentReaderId(contentModuleId);
		contentNode.setCreationDate(date);
		contentNode.setName(name);
		contentNode.setDraft(draft);
		contentNode.setDraftSourceId(draftSourceId);
		contentNode.setPublicated(publicated);

		// Ulož contentNode
		Node parent = new Node();
		parent.setId(nodeId);
		contentNode.setParent(parent);

		User user = new User();
		user.setId(authorId);
		contentNode.setAuthor(user);

		contentNode = contentNodeRepository.save(contentNode);

		// aktualizace tagů
		contentTagService.saveTags(tags, contentNode);

		return contentNode.getId();
	}

	@Override
	public ContentNodeTO getByID(long contentNodeId) {
		ContentNode contentNode = contentNodeRepository.findOne(contentNodeId);
		return mapper.mapContentNodeForDetail(contentNode);
	}

	@Override
	public void modify(long contentNodeId, String name, boolean publicated) {
		modify(contentNodeId, name, null, publicated);
	}

	@Override
	public void modify(long contentNodeId, String name, Collection<String> tags, boolean publicated) {
		modify(contentNodeId, name, tags, publicated, null);
	}

	@Override
	public void modify(long contentNodeId, String name, Collection<String> tags, boolean publicated,
			LocalDateTime creationDate) {
		Validate.notNull(name, "'name' nesmí být null");
		ContentNode contentNode = contentNodeRepository.findOne(contentNodeId);

		contentNode.setLastModificationDate(LocalDateTime.now());
		contentNode.setName(name);
		contentNode.setPublicated(publicated);

		if (creationDate != null)
			contentNode.setCreationDate(creationDate);

		// Ulož změny v contentNode
		contentNodeRepository.save(contentNode);
		// aktualizace tagů
		contentTagService.saveTags(tags, contentNodeId);
	}

	@Override
	public void deleteByContentNodeId(long contentNodeId) {
		userService.removeContentFromAllUsersFavourites(contentNodeId);

		// vymaž tagy
		contentTagService.saveTags(null, contentNodeId);

		// vymaž content node
		ContentNode contentNode = contentNodeRepository.findOne(contentNodeId);
		contentNodeRepository.delete(contentNode);
	}

	@Override
	public void deleteByContentId(String contentModuleId, long contentId) {
		Validate.notNull(contentModuleId, "'contentModuleId' nemůže být null");
		Long contentNodeId = contentNodeRepository.findIdByContentModuleAndContentId(contentModuleId, contentId);
		if (contentNodeId != null)
			deleteByContentNodeId(contentNodeId);
		else
			throw new IllegalStateException("Dle ID koncového obsahu nebyl nalezen obecný uzel obsahu");
	}

	@Override
	public void moveContent(long nodeId, long contentNodeId) {
		contentNodeRepository.moveContent(nodeId, contentNodeId);
	}

	/**
	 * Všechny obsahy
	 */

	private QueryResults<ContentNodeOverviewTO> innerByUserAccess(PageRequest pr) {
		UserInfoTO user = securityService.getCurrentUser();
		return contentNodeRepository.findByUserAccess(user.getId(), user.isAdmin(), pr);
	}

	@Override
	public int getCount() {
		return (int) innerByUserAccess(new PageRequest(1, 1)).getTotal();
	}

	@Override
	public List<ContentNodeOverviewTO> getRecentAdded(int offset, int limit) {
		return innerByUserAccess(QuerydslUtil.transformOffsetLimit(offset, limit, Sort.Direction.DESC, "creationDate"))
				.getResults();
	}

	@Override
	public List<ContentNodeOverviewTO> getRecentModified(int offset, int limit) {
		return innerByUserAccess(
				QuerydslUtil.transformOffsetLimit(offset, limit, Sort.Direction.DESC, "lastModificationDate"))
						.getResults();
	}

	/**
	 * Dle tagu
	 */

	private QueryResults<ContentNodeOverviewTO> innerByTagAndUserAccess(long tagId, PageRequest pr) {
		UserInfoTO user = securityService.getCurrentUser();
		return contentNodeRepository.findByTagAndUserAccess(tagId, user.getId(), user.isAdmin(), pr);
	}

	@Override
	public int getCountByTag(long tagId) {
		return (int) innerByTagAndUserAccess(tagId, new PageRequest(1, 1)).getTotal();
	}

	@Override
	public List<ContentNodeOverviewTO> getByTag(long tagId, int offset, int limit) {
		return innerByTagAndUserAccess(tagId,
				QuerydslUtil.transformOffsetLimit(offset, limit, Sort.Direction.DESC, "creationDate")).getResults();
	}

	/**
	 * Dle oblíbených uživatele
	 */

	private QueryResults<ContentNodeOverviewTO> innerByUserFavouritesAndUserAccess(long userId, Pageable pr) {
		UserInfoTO user = securityService.getCurrentUser();
		return contentNodeRepository.findByUserFavouritesAndUserAccess(userId, user.getId(), user.isAdmin(), pr);
	}

	@Override
	public int getUserFavouriteCount(long userId) {
		return (int) innerByUserFavouritesAndUserAccess(userId, new PageRequest(1, 1)).getTotal();
	}

	@Override
	public List<ContentNodeOverviewTO> getUserFavourite(long userId, int offset, int limit) {
		return innerByUserFavouritesAndUserAccess(userId, QuerydslUtil.transformOffsetLimit(offset, limit))
				.getResults();
	}

	/**
	 * Dle kategorie
	 */

	private QueryResults<ContentNodeOverviewTO> innerByNodeAndUserAccess(long nodeId, PageRequest pr) {
		UserInfoTO user = securityService.getCurrentUser();
		return contentNodeRepository.findByNodeAndUserAccess(nodeId, user.getId(), user.isAdmin(), pr);
	}

	@Override
	public int getCountByNode(long nodeId) {
		return (int) innerByNodeAndUserAccess(nodeId, new PageRequest(1, 1)).getTotal();
	}

	@Override
	public List<ContentNodeOverviewTO> getByNode(long nodeId, int offset, int limit) {
		return innerByNodeAndUserAccess(nodeId, QuerydslUtil.transformOffsetLimit(offset, limit)).getResults();
	}

	/**
	 * Dle názvu
	 */

	private QueryResults<ContentNodeOverviewTO> innerByNameAndUserAccess(String name, Long userId, boolean isAdmin,
			PageRequest pr) {
		name = "%" + name.replace('*', '%') + "%";
		return contentNodeRepository.findByNameAndUserAccess(name, userId, isAdmin, pr);
	}

	@Override
	public int getCountByName(String name, Long userId) {
		boolean isAdmin = userRepository.findOne(userId).isAdmin();
		return (int) innerByNameAndUserAccess(name, userId, isAdmin, new PageRequest(1, 1)).getTotal();
	}

	@Override
	public List<ContentNodeOverviewTO> getByName(String name, Long userId, PageRequest pr) {
		boolean isAdmin = userRepository.findOne(userId).isAdmin();
		return innerByNameAndUserAccess(name, userId, isAdmin, pr).getResults();
	}

	@Override
	public List<ContentNodeOverviewTO> getByName(String name, Long userId, int offset, int limit) {
		boolean isAdmin = userRepository.findOne(userId).isAdmin();
		return innerByNameAndUserAccess(name, userId, isAdmin, QuerydslUtil.transformOffsetLimit(offset, limit))
				.getResults();
	}

}
