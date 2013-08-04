package org.myftp.gattserver.grass3.model.dto;

import java.util.Set;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami
 * 
 * @author gatt
 * 
 */
public class ContentTagDTO implements Comparable<ContentTagDTO> {

	/**
	 * Název tagu
	 */
	private String name;

	/**
	 * Obsahy tagu
	 */
	private Set<ContentNodeDTO> contentNodes;

	/**
	 * Počet obsahů k tagu
	 */
	private int contentNodesCount;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public ContentTagDTO() {
	}

	public ContentTagDTO(String name) {
		this.name = name;
	}

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

	public Set<ContentNodeDTO> getContentNodes() {
		return contentNodes;
	}

	public void setContentNodes(Set<ContentNodeDTO> contentNodes) {
		this.contentNodes = contentNodes;
	}

	public int getContentNodesCount() {
		return contentNodesCount;
	}

	public void setContentNodesCount(int contentNodesCount) {
		this.contentNodesCount = contentNodesCount;
	}

	@Override
	public int compareTo(ContentTagDTO o) {
		return getName().compareTo(o.getName());
	}

}
