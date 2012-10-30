package org.myftp.gattserver.grass3.model.dto;

import java.util.List;

public class NodeDTO {

	/**
	 * Název uzlu
	 */
	private String name;

	/**
	 * Předek uzlu
	 */
	private Long parentID;

	/**
	 * Potomci uzlu
	 */
	private List<Long> subNodeIDs;

	/**
	 * Obsahy uzlu
	 */
	private List<ContentNodeDTO> contentNodes;

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

	public Long getParentID() {
		return parentID;
	}

	public void setParentID(Long parentID) {
		this.parentID = parentID;
	}

	public List<Long> getSubNodeIDs() {
		return subNodeIDs;
	}

	public void setSubNodeIDs(List<Long> subNodeIDs) {
		this.subNodeIDs = subNodeIDs;
	}

	public List<ContentNodeDTO> getContentNodes() {
		return contentNodes;
	}

	public void setContentNodes(List<ContentNodeDTO> contentNodes) {
		this.contentNodes = contentNodes;
	}

}
