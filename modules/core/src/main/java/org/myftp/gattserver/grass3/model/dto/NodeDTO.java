package org.myftp.gattserver.grass3.model.dto;

import java.util.HashSet;
import java.util.Set;

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
	private Set<Long> subNodeIDs = new HashSet<Long>();

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

	public Long getParentID() {
		return parentID;
	}

	public void setParentID(Long parentID) {
		this.parentID = parentID;
	}

	public Set<Long> getSubNodeIDs() {
		return subNodeIDs;
	}

	public void setSubNodeIDs(Set<Long> subNodeIDs) {
		this.subNodeIDs = subNodeIDs;
	}

	public Set<ContentNodeDTO> getContentNodes() {
		return contentNodes;
	}

	public void setContentNodes(Set<ContentNodeDTO> contentNodes) {
		this.contentNodes = contentNodes;
	}

}
