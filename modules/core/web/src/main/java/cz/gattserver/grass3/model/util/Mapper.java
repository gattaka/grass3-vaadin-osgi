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
import cz.gattserver.grass3.model.dto.ContentTagDTO;
import cz.gattserver.grass3.model.dto.NodeBreadcrumbDTO;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.model.dto.NodeOverviewDTO;
import cz.gattserver.grass3.model.dto.QuoteDTO;
import cz.gattserver.grass3.model.dto.UserInfoDTO;

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
	 * Převede {@link ContentNode} na {@link ContentNodeOverviewDTO}
	 * 
	 * @param e
	 * @return
	 */
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

	/**
	 * Převede {@link ContentNode} na {@link ContentNodeDTO}, používá se pro
	 * detail obsahu, kde je potřeba rekurzivní mapování parentů do breadcrumb
	 * 
	 * @param e
	 * @return
	 */
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
		contentNodeDTO.setContentTags(mapContentTagCollectionForOverview(e.getContentTags()));
		contentNodeDTO.setParent(mapNodeForBreadcrumb(e.getParent()));

		return contentNodeDTO;
	}

	/**
	 * Převede set {@link ContentNode} na list {@link ContentNodeDTO}
	 * 
	 * @param contentNodes
	 * @return
	 */
	public List<ContentNodeOverviewDTO> mapContentNodeOverviewCollection(Collection<ContentNode> contentNodes) {
		if (contentNodes == null)
			return null;

		List<ContentNodeOverviewDTO> contentNodeDTOs = new ArrayList<ContentNodeOverviewDTO>();
		for (ContentNode contentNode : contentNodes) {
			contentNodeDTOs.add(mapContentNodeOverview(contentNode));
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
		contentTagDTO.setContentNodesCount(e.getContentNodesCount());

		return contentTagDTO;
	}

	public ContentTagDTO mapContentTag(ContentTag e) {
		if (e == null)
			return null;

		ContentTagDTO contentTagDTO = new ContentTagDTO();

		contentTagDTO.setId(e.getId());
		contentTagDTO.setName(e.getName());
		contentTagDTO.setContentNodes(mapContentNodeOverviewCollection(e.getContentNodes()));

		return contentTagDTO;
	}

	/**
	 * Převede list {@link ContentTag} na list {@link ContentTagDTO}
	 * 
	 * @param contentTags
	 * @return
	 */
	public List<ContentTagDTO> mapContentTagCollection(Collection<ContentTag> contentTags) {
		if (contentTags == null)
			return null;

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
	public Set<ContentTagDTO> mapContentTagCollectionForOverview(Collection<ContentTag> contentTags) {
		if (contentTags == null)
			return null;

		Set<ContentTagDTO> contentTagDTOs = new HashSet<ContentTagDTO>();
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
	public NodeDTO mapNodeForDetail(Node e) {
		if (e == null)
			return null;

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(e.getId());
		nodeDTO.setName(e.getName());
		nodeDTO.setParent(mapNodeForBreadcrumb(e.getParent()));
		nodeDTO.setContentNodes(mapContentNodeOverviewCollection(e.getContentNodes()));
		nodeDTO.setSubNodes(mapNodesForOverview(e.getSubNodes()));

		return nodeDTO;
	}

	/**
	 * Pro breadcrumb je potřeba id, název, ale navíc i rekurzivně to samé pro
	 * parenta
	 */
	private NodeBreadcrumbDTO mapNodeForBreadcrumb(Node e) {
		if (e == null)
			return null;

		NodeBreadcrumbDTO nodeDTO = new NodeBreadcrumbDTO();

		nodeDTO.setId(e.getId());
		nodeDTO.setName(e.getName());
		nodeDTO.setParent(mapNodeForBreadcrumb(e.getParent()));

		return nodeDTO;
	}

	/**
	 * Pro overview je potřeba akorát id + název
	 */
	public NodeDTO mapNodeForOverview(Node e) {
		if (e == null)
			return null;

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(e.getId());
		nodeDTO.setName(e.getName());

		return nodeDTO;
	}

	/**
	 * Převede list {@link Node} na list {@link NodeDTO}
	 * 
	 * @param nodes
	 * @return
	 */
	public List<NodeDTO> mapNodesForOverview(Collection<Node> nodes) {
		if (nodes == null)
			return null;

		List<NodeDTO> nodeDTOs = new ArrayList<NodeDTO>();
		for (Node node : nodes) {
			nodeDTOs.add(mapNodeForOverview(node));
		}
		return nodeDTOs;
	}
}