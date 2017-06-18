package cz.gattserver.grass3.model.domain;

import java.util.Set;

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
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

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
	 * Počet obsahů přiřazených k tomuto tagu (aplikace je pak mnohem rychlejší
	 * když se to nemusí počítat)
	 */
	private Integer contentNodesCount;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContentTag))
			return false;
		return ((ContentTag) obj).getName().equals(getName());
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

	public Integer getContentNodesCount() {
		return contentNodesCount;
	}

	public void setContentNodesCount(Integer contentNodesCount) {
		this.contentNodesCount = contentNodesCount;
	}

}
