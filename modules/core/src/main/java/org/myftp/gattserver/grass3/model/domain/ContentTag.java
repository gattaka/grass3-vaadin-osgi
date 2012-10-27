package org.myftp.gattserver.grass3.model.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CONTENT_TAG")
public class ContentTag {

	/**
	 * Název tagu
	 */
	private String name;

	/**
	 * Obsahy tagu
	 */
	@ManyToMany
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

	public List<ContentNode> getContentNodes() {
		return contentNodes;
	}

	public void setContentNodes(List<ContentNode> contentNodes) {
		this.contentNodes = contentNodes;
	}

}
