package cz.gattserver.grass3.model.dto;

import java.util.ArrayList;
import java.util.List;

public class NodeDTO extends NodeBreadcrumbDTO {

	/**
	 * Potomci uzlu
	 */
	private List<NodeDTO> subNodes = new ArrayList<NodeDTO>();

	/**
	 * Obsahy uzlu
	 */
	private List<ContentNodeOverviewDTO> contentNodes = new ArrayList<ContentNodeOverviewDTO>();

	public List<NodeDTO> getSubNodes() {
		return subNodes;
	}

	public void setSubNodes(List<NodeDTO> subNodes) {
		this.subNodes = subNodes;
	}

	public List<ContentNodeOverviewDTO> getContentNodes() {
		return contentNodes;
	}

	public void setContentNodes(List<ContentNodeOverviewDTO> contentNodes) {
		this.contentNodes = contentNodes;
	}

}
