package org.myftp.gattserver.grass3.model.domain;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CONTENT_TAG")
@NamedQuery(name = "findTagByName", query = "SELECT t FROM ContentTag t WHERE t.name LIKE :name")
public class ContentTag {

	/**
	 * Název tagu
	 */
	private String name;

	/**
	 * Obsahy tagu
	 */
	@ManyToMany(mappedBy = "contentTags")
	private Set<ContentNode> contentNodes;

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContentTag))
			return false;
		return ((ContentTag) obj).getName() == getName();
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
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

	public Set<ContentNode> getContentNodes() {
		return contentNodes;
	}

	public void setContentNodes(Set<ContentNode> contentNodes) {
		this.contentNodes = contentNodes;
	}

}
