package cz.gattserver.grass3.model.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.domain.ContentTag;
import cz.gattserver.grass3.model.domain.Node;
import cz.gattserver.grass3.model.domain.Quote;
import cz.gattserver.grass3.model.domain.User;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;
import cz.gattserver.grass3.model.dto.ContentTagOverviewDTO;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.model.dto.NodeOverviewDTO;
import cz.gattserver.grass3.model.dto.QuoteDTO;
import cz.gattserver.grass3.model.dto.UserInfoDTO;

@Component
public class CoreMapperImpl implements CoreMapper {

	@Override
	public UserInfoDTO map(User e) {
		if (e == null)
			return null;

		UserInfoDTO userInfoDTO = new UserInfoDTO();

		userInfoDTO.setConfirmed(e.isConfirmed());
		userInfoDTO.setEmail(e.getEmail());
		userInfoDTO.setId(e.getId());
		userInfoDTO.setLastLoginDate(e.getLastLoginDate());
		userInfoDTO.setName(e.getName());
		userInfoDTO.setPassword(e.getPassword());
		userInfoDTO.setRegistrationDate(e.getRegistrationDate());
		userInfoDTO.setRoles(e.getRoles());

		return userInfoDTO;
	}

	@Override
	public QuoteDTO map(Quote e) {
		if (e == null)
			return null;

		QuoteDTO quoteDTO = new QuoteDTO();

		quoteDTO.setId(e.getId());
		quoteDTO.setName(e.getName());

		return quoteDTO;
	}

	@Override
	public ContentNodeOverviewDTO mapContentNodeOverview(ContentNode e) {
		if (e == null)
			return null;

		ContentNodeOverviewDTO contentNodeDTO = new ContentNodeOverviewDTO();

		contentNodeDTO.setAuthor(map(e.getAuthor()));
		contentNodeDTO.setContentID(e.getContentId());
		contentNodeDTO.setContentReaderID(e.getContentReaderId());
		contentNodeDTO.setCreationDate(e.getCreationDate());
		contentNodeDTO.setId(e.getId());
		contentNodeDTO.setLastModificationDate(e.getLastModificationDate());
		contentNodeDTO.setName(e.getName());
		contentNodeDTO.setPublicated(e.getPublicated());

		NodeOverviewDTO nodeDTO = new NodeOverviewDTO();
		nodeDTO.setId(e.getParent().getId());
		nodeDTO.setName(e.getParent().getName());
		contentNodeDTO.setParent(nodeDTO);

		return contentNodeDTO;
	}

	@Override
	public ContentNodeDTO mapContentNodeForDetail(ContentNode e) {
		if (e == null)
			return null;

		ContentNodeDTO contentNodeDTO = new ContentNodeDTO();

		contentNodeDTO.setAuthor(map(e.getAuthor()));
		contentNodeDTO.setContentID(e.getContentId());
		contentNodeDTO.setContentReaderID(e.getContentReaderId());
		contentNodeDTO.setCreationDate(e.getCreationDate());
		contentNodeDTO.setId(e.getId());
		contentNodeDTO.setLastModificationDate(e.getLastModificationDate());
		contentNodeDTO.setName(e.getName());
		contentNodeDTO.setPublicated(e.getPublicated());
		contentNodeDTO.setDraft(e.getDraft());
		contentNodeDTO.setDraftSourceId(e.getDraftSourceId());
		contentNodeDTO.setContentTags(mapContentTagCollectionForOverview(e.getContentTags()));
		contentNodeDTO.setParent(mapNodeForOverview(e.getParent()));

		return contentNodeDTO;
	}

	@Override
	public List<ContentNodeOverviewDTO> mapContentNodeOverviewCollection(Collection<ContentNode> contentNodes) {
		if (contentNodes == null)
			return null;

		List<ContentNodeOverviewDTO> contentNodeDTOs = new ArrayList<ContentNodeOverviewDTO>();
		for (ContentNode contentNode : contentNodes) {
			contentNodeDTOs.add(mapContentNodeOverview(contentNode));
		}
		return contentNodeDTOs;
	}

	@Override
	public ContentTagOverviewDTO mapContentTagForOverview(ContentTag e) {
		if (e == null)
			return null;

		ContentTagOverviewDTO contentTagDTO = new ContentTagOverviewDTO();

		contentTagDTO.setId(e.getId());
		contentTagDTO.setName(e.getName());
		contentTagDTO.setContentNodesCount(e.getContentNodesCount());

		return contentTagDTO;
	}

	@Override
	public ContentTagOverviewDTO mapContentTag(ContentTag e) {
		if (e == null)
			return null;

		ContentTagOverviewDTO contentTagDTO = new ContentTagOverviewDTO();

		contentTagDTO.setId(e.getId());
		contentTagDTO.setName(e.getName());
		contentTagDTO.setContentNodesCount(e.getContentNodesCount());

		return contentTagDTO;
	}

	@Override
	public List<ContentTagOverviewDTO> mapContentTagCollection(Collection<ContentTag> contentTags) {
		if (contentTags == null)
			return null;

		List<ContentTagOverviewDTO> contentTagDTOs = new ArrayList<ContentTagOverviewDTO>();
		for (ContentTag contentTag : contentTags) {
			contentTagDTOs.add(mapContentTag(contentTag));
		}
		return contentTagDTOs;
	}

	@Override
	public Set<ContentTagOverviewDTO> mapContentTagCollectionForOverview(Collection<ContentTag> contentTags) {
		if (contentTags == null)
			return null;

		Set<ContentTagOverviewDTO> contentTagDTOs = new HashSet<ContentTagOverviewDTO>();
		for (ContentTag contentTag : contentTags) {
			contentTagDTOs.add(mapContentTagForOverview(contentTag));
		}
		return contentTagDTOs;
	}

	@Override
	public NodeDTO mapNodeForDetail(Node e) {
		if (e == null)
			return null;

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(e.getId());
		nodeDTO.setName(e.getName());
		if (e.getParent() != null) {
			nodeDTO.setParentId(e.getParent().getId());
			nodeDTO.setParentName(e.getParent().getName());
			nodeDTO.setParent(mapNodeForDetail(e.getParent()));
		}

		return nodeDTO;
	}

	@Override
	public NodeOverviewDTO mapNodeForOverview(Node e) {
		if (e == null)
			return null;

		NodeOverviewDTO nodeDTO = new NodeOverviewDTO();

		nodeDTO.setId(e.getId());
		nodeDTO.setName(e.getName());
		if (e.getParent() != null) {
			nodeDTO.setParentId(e.getParent().getId());
			nodeDTO.setParentName(e.getParent().getName());
		}

		return nodeDTO;
	}

	@Override
	public List<NodeOverviewDTO> mapNodesForOverview(Collection<Node> nodes) {
		if (nodes == null)
			return null;

		List<NodeOverviewDTO> nodeDTOs = new ArrayList<NodeOverviewDTO>();
		for (Node node : nodes) {
			nodeDTOs.add(mapNodeForOverview(node));
		}
		return nodeDTOs;
	}
}