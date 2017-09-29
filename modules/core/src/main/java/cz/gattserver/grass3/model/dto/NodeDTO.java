package cz.gattserver.grass3.model.dto;

import java.util.ArrayList;
import java.util.List;

public class NodeDTO extends NodeBreadcrumbDTO {

	/**
	 * Potomci uzlu
	 */
	private List<NodeDTO> subNodes = new ArrayList<NodeDTO>();

	public List<NodeDTO> getSubNodes() {
		return subNodes;
	}

	public void setSubNodes(List<NodeDTO> subNodes) {
		this.subNodes = subNodes;
	}

}
