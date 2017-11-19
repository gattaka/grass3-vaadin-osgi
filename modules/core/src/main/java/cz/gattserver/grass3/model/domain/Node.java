package cz.gattserver.grass3.model.domain;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "NODE")
public class Node {

	/**
	 * Název uzlu
	 */
	private String name;

	/**
	 * Předek uzlu
	 */
	@ManyToOne(optional = true)
	private Node parent;

	// /**
	// * Potomci uzlu
	// */
	// @OneToMany
	// private Set<Node> subNodes;
	//
	// /**
	// * Obsahy uzlu
	// */
	// @OneToMany
	// private Set<ContentNode> contentNodes;

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Node))
			return false;
		return ((Node) obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
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

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	// public Set<Node> getSubNodes() {
	// return subNodes;
	// }
	//
	// public void setSubNodes(Set<Node> subNodes) {
	// this.subNodes = subNodes;
	// }
	//
	// public Set<ContentNode> getContentNodes() {
	// return contentNodes;
	// }
	//
	// public void setContentNodes(Set<ContentNode> contentNodes) {
	// this.contentNodes = contentNodes;
	// }

}
