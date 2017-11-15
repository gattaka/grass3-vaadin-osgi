package cz.gattserver.grass3.model.dto;

public class NodeDTO extends NodeOverviewDTO {

	private NodeDTO parent;

	public NodeDTO getParent() {
		return parent;
	}

	public void setParent(NodeDTO parent) {
		this.parent = parent;
	}

}
