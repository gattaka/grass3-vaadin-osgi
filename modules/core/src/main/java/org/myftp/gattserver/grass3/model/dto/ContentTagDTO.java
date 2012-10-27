package org.myftp.gattserver.grass3.model.dto;

import java.util.List;

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

	public List<ContentNodeDTO> getContentNodes() {
		return contentNodes;
	}

	public void setContentNodes(List<ContentNodeDTO> contentNodes) {
		this.contentNodes = contentNodes;
	}

}
