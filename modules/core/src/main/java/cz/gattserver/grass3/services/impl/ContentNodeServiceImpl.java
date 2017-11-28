package cz.gattserver.grass3.services.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass3.interfaces.ContentNodeTO;
import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.domain.Node;
import cz.gattserver.grass3.model.domain.User;
import cz.gattserver.grass3.model.repositories.ContentNodeRepository;
import cz.gattserver.grass3.model.util.CoreMapper;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.services.UserService;

@Transactional
@Service
public class ContentNodeServiceImpl implements ContentNodeService {

	@Autowired
	private CoreMapper mapper;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private ContentTagService contentTagService;

	@Autowired
	private UserService userService;

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
		ContentNodeTO contentNodeDTO = mapper.mapContentNodeForDetail(contentNode);
		return contentNodeDTO;
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
		if (contentNodeId != null) {
			deleteByContentNodeId(contentNodeId);
		} else {
			throw new IllegalStateException("Dle ID koncového obsahu nebyl nalezen obecný uzel obsahu");
		}
	}

	@Override
	public void moveContent(long nodeId, long contentNodeId) {
		contentNodeRepository.moveContent(nodeId, contentNodeId);
	}

	/**
	 * Všechny obsahy
	 */

	private Page<ContentNode> innerByUserAccess(PageRequest pr) {
		UserInfoTO user = securityService.getCurrentUser();
		return contentNodeRepository.findByUserAccess(user.getId(), user.isAdmin(), pr);
	}

	@Override
	public int getCount() {
		return (int) innerByUserAccess(new PageRequest(1, 1)).getTotalElements();
	}

	@Override
	public List<ContentNodeOverviewTO> getRecentAdded(int pageIndex, int count) {
		return mapper.mapContentNodeOverviewCollection(
				innerByUserAccess(new PageRequest(pageIndex, count, Sort.Direction.DESC, "creationDate")).getContent());
	}

	@Override
	public List<ContentNodeOverviewTO> getRecentModified(int pageIndex, int count) {
		return mapper.mapContentNodeOverviewCollection(
				innerByUserAccess(new PageRequest(pageIndex, count, Sort.Direction.DESC, "lastModificationDate"))
						.getContent());
	}

	/**
	 * Dle tagu
	 */

	private Page<ContentNode> innerByTagAndUserAccess(long tagId, PageRequest pr) {
		UserInfoTO user = securityService.getCurrentUser();
		return contentNodeRepository.findByTagAndUserAccess(tagId, user.getId(), user.isAdmin(), pr);
	}

	@Override
	public int getCountByTag(long tagId) {
		return (int) innerByTagAndUserAccess(tagId, new PageRequest(1, 1)).getTotalElements();
	}

	@Override
	public List<ContentNodeOverviewTO> getByTag(long tagId, int pageIndex, int count) {
		return mapper.mapContentNodeOverviewCollection(
				innerByTagAndUserAccess(tagId, new PageRequest(pageIndex, count, Sort.Direction.DESC, "creationDate"))
						.getContent());
	}

	/**
	 * Dle oblíbených uživatele
	 */

	private Page<ContentNode> innerByUserFavouritesAndUserAccess(long userId, PageRequest pr) {
		UserInfoTO user = securityService.getCurrentUser();
		return contentNodeRepository.findByUserFavouritesAndUserAccess(userId, user.getId(), user.isAdmin(), pr);
	}

	@Override
	public int getUserFavouriteCount(long userId) {
		return (int) innerByUserFavouritesAndUserAccess(userId, new PageRequest(1, 1)).getTotalElements();
	}

	@Override
	public List<ContentNodeOverviewTO> getUserFavourite(long userId, int pageIndex, int count) {
		return mapper.mapContentNodeOverviewCollection(
				innerByUserFavouritesAndUserAccess(userId, new PageRequest(pageIndex, count)).getContent());
	}

	/**
	 * Dle kategorie
	 */

	private Page<ContentNode> innerByNodeAndUserAccess(long nodeId, PageRequest pr) {
		UserInfoTO user = securityService.getCurrentUser();
		return contentNodeRepository.findByNodeAndUserAccess(nodeId, user.getId(), user.isAdmin(), pr);
	}

	@Override
	public int getCountByNode(long nodeId) {
		return (int) innerByNodeAndUserAccess(nodeId, new PageRequest(1, 1)).getTotalElements();
	}

	@Override
	public List<ContentNodeOverviewTO> getByNode(long nodeId, int pageIndex, int count) {
		return mapper.mapContentNodeOverviewCollection(
				innerByNodeAndUserAccess(nodeId, new PageRequest(pageIndex, count)).getContent());
	}

}