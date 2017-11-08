package cz.gattserver.grass3.model.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.domain.ContentTag;
import cz.gattserver.grass3.model.domain.Node;
import cz.gattserver.grass3.model.domain.Quote;
import cz.gattserver.grass3.model.domain.User;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;
import cz.gattserver.grass3.model.dto.ContentTagDTO;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.model.dto.NodeTreeDTO;
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
public interface CoreMapper {

	/**
	 * Převede {@link User} na {@link UserInfoDTO}
	 * 
	 * @param e
	 * @return
	 */
	public UserInfoDTO map(User e);

	/**
	 * Převede {@link Quote} na {@link QuoteDTO}
	 * 
	 * @param e
	 * @return
	 */
	public QuoteDTO map(Quote e);

	/**
	 * Převede {@link ContentNode} na {@link ContentNodeOverviewDTO}
	 * 
	 * @param e
	 * @return
	 */
	public ContentNodeOverviewDTO mapContentNodeOverview(ContentNode e);

	/**
	 * Převede {@link ContentNode} na {@link ContentNodeDTO}, používá se pro
	 * detail obsahu, kde je potřeba rekurzivní mapování parentů do breadcrumb
	 * 
	 * @param e
	 * @return
	 */
	public ContentNodeDTO mapContentNodeForDetail(ContentNode e);

	/**
	 * Převede set {@link ContentNode} na list {@link ContentNodeDTO}
	 * 
	 * @param contentNodes
	 * @return
	 */
	public List<ContentNodeOverviewDTO> mapContentNodeOverviewCollection(Collection<ContentNode> contentNodes);

	/**
	 * Převede {@link ContentTag} na {@link ContentTagDTO}
	 * 
	 * @param e
	 * @return
	 */
	public ContentTagDTO mapContentTagForOverview(ContentTag e);

	public ContentTagDTO mapContentTag(ContentTag e);

	/**
	 * Převede list {@link ContentTag} na list {@link ContentTagDTO}
	 * 
	 * @param contentTags
	 * @return
	 */
	public List<ContentTagDTO> mapContentTagCollection(Collection<ContentTag> contentTags);

	/**
	 * Převede list {@link ContentTag} na list {@link ContentTagDTO}
	 * 
	 * @param contentTags
	 * @return
	 */
	public Set<ContentTagDTO> mapContentTagCollectionForOverview(Collection<ContentTag> contentTags);

	/**
	 * Převede {@link Node} na {@link NodeDTO}
	 * 
	 * @param e
	 * @return
	 */
	public NodeDTO mapNodeForDetail(Node e);

	/**
	 * Pro overview je potřeba akorát id + název
	 */
	public NodeDTO mapNodeForOverview(Node e);

	/**
	 * Převede list {@link Node} na list {@link NodeDTO}
	 * 
	 * @param nodes
	 * @return
	 */
	public List<NodeDTO> mapNodesForOverview(Collection<Node> nodes);

	/**
	 * Pro tree je potřeba akorát id, název a id parenta
	 */
	public NodeTreeDTO mapNodeForTree(Node e);

	/**
	 * Převede list {@link Node} na list {@link NodeTreeDTO}
	 * 
	 * @param nodes
	 * @return
	 */
	public List<NodeTreeDTO> mapNodesForTree(Collection<Node> nodes);
}