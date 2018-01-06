package cz.gattserver.grass3.articles.model.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "ARTICLE_JS_RESOURCE")
public class ArticleJSResource implements Comparable<ArticleJSResource> {

	/**
	 * Jméno skriptu
	 */
	private String name;

	/**
	 * Pořadí při nahrávání
	 */
	private Integer executionOrder = 0;

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

	public Integer getExecutionOrder() {
		return executionOrder;
	}

	public void setExecutionOrder(Integer executionOrder) {
		this.executionOrder = executionOrder;
	}

	@Override
	public int compareTo(ArticleJSResource resource) {
		return this.getExecutionOrder().compareTo(resource.getExecutionOrder());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof ArticleJSResource) {
			ArticleJSResource other = (ArticleJSResource) obj;
			if (getName() == null) {
				if (other.getName() != null)
					return false;
			} else if (!getName().equals(other.getName()))
				return false;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Name: " + name + " Order: " + executionOrder;
	}

}
