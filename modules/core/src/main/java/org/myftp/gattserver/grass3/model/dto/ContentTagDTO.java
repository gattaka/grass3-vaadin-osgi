package org.myftp.gattserver.grass3.model.dto;

import java.util.Set;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami
 * 
 * @author gatt
 * 
 */
public class ContentTagDTO {

	/**
	 * Název tagu
	 */
	private String name;

	/**
	 * Obsahy tagu
	 */
	private Set<ContentNodeDTO> contentNodes;

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

	public Set<ContentNodeDTO> getContentNodes() {
		return contentNodes;
	}

	public void setContentNodes(Set<ContentNodeDTO> contentNodes) {
		this.contentNodes = contentNodes;
	}

}
