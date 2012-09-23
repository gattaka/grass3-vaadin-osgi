package org.myftp.gattserver.grass3.model.domain;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "NODE")
public class Node {

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
	private List<Long> contentNodeIDs;

	/**
	 * DB identifikátor
	 */
	private Long id;

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
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

	@Column(name = "parent_id")
	public Long getParentID() {
		return parentID;
	}

	public void setParentID(Long parentID) {
		this.parentID = parentID;
	}

	@ElementCollection
	@CollectionTable(name = "node_node", joinColumns = @JoinColumn(name = "parent_node_id"))
	@Column(name = "child_node_id")
	public List<Long> getSubNodeIDs() {
		return subNodeIDs;
	}

	public void setSubNodeIDs(List<Long> subNodeIDs) {
		this.subNodeIDs = subNodeIDs;
	}

	@ElementCollection
	@CollectionTable(name = "contentnode", joinColumns = @JoinColumn(name = "parent_id"))
	@Column(name = "id")
	public List<Long> getContentNodeIDs() {
		return contentNodeIDs;
	}

	public void setContentNodeIDs(List<Long> contentNodeIDs) {
		this.contentNodeIDs = contentNodeIDs;
	}

}
