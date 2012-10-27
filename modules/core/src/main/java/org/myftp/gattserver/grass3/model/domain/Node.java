package org.myftp.gattserver.grass3.model.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
	@ManyToOne
	private Node parent;

	/**
	 * Potomci uzlu
	 */
	@OneToMany
	private List<Node> subNodes;

	/**
	 * Obsahy uzlu
	 */
	@OneToMany
	private List<ContentNode> contentNodes;

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
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

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public List<Node> getSubNodes() {
		return subNodes;
	}

	public void setSubNodes(List<Node> subNodes) {
		this.subNodes = subNodes;
	}

	public List<ContentNode> getContentNodes() {
		return contentNodes;
	}

	public void setContentNodes(List<ContentNode> contentNodes) {
		this.contentNodes = contentNodes;
	}

}
