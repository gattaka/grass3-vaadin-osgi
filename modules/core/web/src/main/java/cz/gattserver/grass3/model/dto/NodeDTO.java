package cz.gattserver.grass3.model.dto;

import java.util.ArrayList;
import java.util.List;

public class NodeDTO {

	/**
	 * Název uzlu
	 */
	private String name;

	/**
	 * Předek uzlu
	 */
	private NodeDTO parent;

	/**
	 * Potomci uzlu
	 */
	private List<NodeDTO> subNodes = new ArrayList<NodeDTO>();

	/**
	 * Obsahy uzlu
	 */
	private List<ContentNodeDTO> contentNodes = new ArrayList<ContentNodeDTO>();

	/**
	 * DB identifikátor
	 */
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NodeDTO getParent() {
		return parent;
	}

	public void setParent(NodeDTO parent) {
		this.parent = parent;
	}

	public List<NodeDTO> getSubNodes() {
		return subNodes;
	}

	public void setSubNodes(List<NodeDTO> subNodes) {
		this.subNodes = subNodes;
	}

	public List<ContentNodeDTO> getContentNodes() {
		return contentNodes;
	}

	public void setContentNodes(List<ContentNodeDTO> contentNodes) {
		this.contentNodes = contentNodes;
	}

}
