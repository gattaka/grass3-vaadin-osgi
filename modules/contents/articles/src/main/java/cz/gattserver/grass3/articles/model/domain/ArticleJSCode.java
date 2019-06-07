package cz.gattserver.grass3.articles.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "ARTICLE_JS_CODE")
public class ArticleJSCode implements Comparable<ArticleJSCode> {

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	/**
	 * Obsah skriptu
	 */
	@Column(columnDefinition = "TEXT")
	private String content;

	/**
	 * Pořadí při nahrávání
	 */
	private Integer executionOrder = 0;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getExecutionOrder() {
		return executionOrder;
	}

	public void setExecutionOrder(Integer executionOrder) {
		this.executionOrder = executionOrder;
	}

	@Override
	public int compareTo(ArticleJSCode resource) {
		return this.getExecutionOrder().compareTo(resource.getExecutionOrder());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof ArticleJSCode) {
			ArticleJSCode other = (ArticleJSCode) obj;
			if (getContent() == null) {
				if (other.getContent() != null)
					return false;
			} else if (!getContent().equals(other.getContent()))
				return false;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Order: " + executionOrder;
	}

}
