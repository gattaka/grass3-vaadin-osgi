package org.myftp.gattserver.grass3.model.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private Set<ContentNodeDTO> contentNodes = new HashSet<ContentNodeDTO>();

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

	public Set<ContentNodeDTO> getContentNodes() {
		return contentNodes;
	}

	public void setContentNodes(Set<ContentNodeDTO> contentNodes) {
		this.contentNodes = contentNodes;
	}

}
