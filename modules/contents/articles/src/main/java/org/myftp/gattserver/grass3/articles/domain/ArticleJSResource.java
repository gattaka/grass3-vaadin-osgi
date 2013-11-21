package org.myftp.gattserver.grass3.articles.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "ARTICLE_JS_RESOURCE")
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

	public ArticleJSResource() {
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
	public String toString() {
		return "Name: " + name + " Order: " + executionOrder;
	}

}
