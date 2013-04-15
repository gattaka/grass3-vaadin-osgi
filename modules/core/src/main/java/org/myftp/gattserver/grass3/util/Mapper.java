package org.myftp.gattserver.grass3.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.model.dao.ContentTagDAO;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.domain.ContentTag;
import org.myftp.gattserver.grass3.model.domain.Node;
import org.myftp.gattserver.grass3.model.domain.Quote;
import org.myftp.gattserver.grass3.model.domain.User;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.ContentTagDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.model.dto.QuoteDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.springframework.stereotype.Component;

/**
 * <b>Mapper pro různé typy.</b>
 * 
 * <p>
 * Je potřeba aby byl volán na objektech s aktivními proxy objekty. To znamená,
 * že před tímto mapperem nedošlo k uzavření session, ve které byl původní
 * objekt pořízen.
 * </p>
 * 
 * <p>
 * Mapper využívá proxy objekty umístěné v atributech předávaných entit. Během
 * mapování tak může docházet k dotazům na DB, které produkují tyto proxy
 * objekty a které se bez původní session mapovaného objektu neobejdou.
 * </p>
 * 
 * @author gatt
 * 
 */
@Component("mapper")
public class Mapper {

	@Resource(name = "contentTagDAO")
	private ContentTagDAO contentTagDAO;

	/**
	 * Převede {@link User} na {@link UserInfoDTO}
	 * 
	 * @param e
	 * @return
	 */
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

	/**
	 * Převede {@link Quote} na {@link QuoteDTO}
	 * 
	 * @param e
	 * @return
	 */
	public QuoteDTO map(Quote e) {
		if (e == null)
			return null;
		
		QuoteDTO quoteDTO = new QuoteDTO();

		quoteDTO.setId(e.getId());
		quoteDTO.setName(e.getName());

		return quoteDTO;
	}

	/**
	 * Převede {@link ContentNode} na {@link ContentNodeDTO}
	 * 
	 * @param e
	 * @return
	 */
	public ContentNodeDTO mapContentNodeForOverview(ContentNode e) {
		if (e == null)
			return null;
		
		ContentNodeDTO contentNodeDTO = new ContentNodeDTO();

		contentNodeDTO.setAuthor(map(e.getAuthor()));
		contentNodeDTO.setContentID(e.getContentId());
		contentNodeDTO.setContentReaderID(e.getContentReaderId());
		contentNodeDTO.setCreationDate(e.getCreationDate());
		contentNodeDTO.setId(e.getId());
		contentNodeDTO.setLastModificationDate(e
				.getLastModificationDate());
		contentNodeDTO.setName(e.getName());

		NodeDTO nodeDTO = new NodeDTO();
		nodeDTO.setId(e.getParent().getId());
		nodeDTO.setName(e.getParent().getName());
		contentNodeDTO.setParent(nodeDTO);

		contentNodeDTO.setPublicated(e.getPublicated());

		return contentNodeDTO;
	}

	public ContentNodeDTO map(ContentNode e) {
		if (e == null)
			return null;
		
		ContentNodeDTO contentNodeDTO = mapContentNodeForOverview(e);

		Set<String> tags = new HashSet<String>();
		for (ContentTag contentTag : e.getContentTags()) {
			tags.add(contentTag.getName());
		}
		contentNodeDTO.setContentTags(tags);

		return contentNodeDTO;
	}

	/**
	 * Převede set {@link ContentNode} na list {@link ContentNodeDTO}
	 * 
	 * @param contentNodes
	 * @return
	 */
	public Set<ContentNodeDTO> mapContentNodeCollection(
			Collection<ContentNode> contentNodes) {
		Set<ContentNodeDTO> contentNodeDTOs = new HashSet<ContentNodeDTO>();
		for (ContentNode contentNode : contentNodes) {
			contentNodeDTOs.add(map(contentNode));
		}
		return contentNodeDTOs;
	}

	public Set<ContentNodeDTO> mapContentNodeCollectionForOverview(
			Collection<ContentNode> contentNodes) {
		Set<ContentNodeDTO> contentNodeDTOs = new HashSet<ContentNodeDTO>();
		for (ContentNode contentNode : contentNodes) {
			contentNodeDTOs.add(mapContentNodeForOverview(contentNode));
		}
		return contentNodeDTOs;
	}

	/**
	 * Převede {@link ContentTag} na {@link ContentTagDTO}
	 * 
	 * @param e
	 * @return
	 */
	public ContentTagDTO mapContentTagForOverview(ContentTag e) {
		if (e == null)
			return null;
		
		ContentTagDTO contentTagDTO = new ContentTagDTO();

		contentTagDTO.setId(e.getId());
		contentTagDTO.setName(e.getName());
		// contentTagDTO.setContentSize(contentTag.getContentNodes().size());
		contentTagDTO.setContentSize(contentTagDAO
				.getCountOfTagContents(e.getId()));

		return contentTagDTO;
	}

	public ContentTagDTO mapContentTag(ContentTag e) {
		if (e == null)
			return null;
		
		ContentTagDTO contentTagDTO = new ContentTagDTO();

		contentTagDTO.setId(e.getId());
		contentTagDTO.setName(e.getName());
		contentTagDTO.setContentNodes(mapContentNodeCollection(e
				.getContentNodes()));

		return contentTagDTO;
	}

	/**
	 * Převede list {@link ContentTag} na list {@link ContentTagDTO}
	 * 
	 * @param contentTags
	 * @return
	 */
	public List<ContentTagDTO> mapContentTagCollection(
			Collection<ContentTag> contentTags) {
		List<ContentTagDTO> contentTagDTOs = new ArrayList<ContentTagDTO>();
		for (ContentTag contentTag : contentTags) {
			contentTagDTOs.add(mapContentTag(contentTag));
		}
		return contentTagDTOs;
	}

	/**
	 * Převede list {@link ContentTag} na list {@link ContentTagDTO}
	 * 
	 * @param contentTags
	 * @return
	 */
	public List<ContentTagDTO> mapContentTagCollectionForOverview(
			Collection<ContentTag> contentTags) {
		List<ContentTagDTO> contentTagDTOs = new ArrayList<ContentTagDTO>();
		for (ContentTag contentTag : contentTags) {
			contentTagDTOs.add(mapContentTagForOverview(contentTag));
		}
		return contentTagDTOs;
	}

	/**
	 * Převede {@link Node} na {@link NodeDTO}
	 * 
	 * @param e
	 * @return
	 */
	public NodeDTO map(Node e) {
		if (e == null)
			return null;
		
		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(e.getId());
		nodeDTO.setName(e.getName());
		nodeDTO.setParentID(e.getParent() == null ? null : e.getParent()
				.getId());
		nodeDTO.setContentNodes(mapContentNodeCollection(e.getContentNodes()));

		return nodeDTO;
	}

	/**
	 * Převede list {@link Node} na list {@link NodeDTO}
	 * 
	 * @param nodes
	 * @return
	 */
	public List<NodeDTO> mapNodeCollection(Collection<Node> nodes) {
		List<NodeDTO> nodeDTOs = new ArrayList<NodeDTO>();
		for (Node node : nodes) {
			nodeDTOs.add(map(node));
		}
		return nodeDTOs;
	}
}
