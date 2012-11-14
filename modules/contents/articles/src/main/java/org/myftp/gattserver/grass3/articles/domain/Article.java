package org.myftp.gattserver.grass3.articles.domain;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.myftp.gattserver.grass3.model.domain.ContentNode;

@Entity
@Table(name = "ARTICLE")
public class Article {

	/**
	 * Obsah článku
	 */
	@Column(columnDefinition = "TEXT")
	private String text;

	/**
	 * Přeložený obsah článku
	 */
	@Column(columnDefinition = "TEXT")
	private String outputHTML;

	/**
	 * Meta-informace o obsahu
	 */
	@OneToOne
	private ContentNode contentNode;

	/**
	 * Dodatečné CSS resources, které je potřeba nahrát (od pluginů)
	 */
	@ElementCollection
	@CollectionTable(name = "ARTICLE_CSS_RESOURCES")
	@Column(name = "pluginCSSResources")
	private Set<String> pluginCSSResources;

	/**
	 * Dodatečné JS resources, které je potřeba nahrát (od pluginů)
	 */
	@ElementCollection
	@CollectionTable(name = "ARTICLE_JS_RESOURCES")
	@Column(name = "pluginJSResources")
	private Set<String> pluginJSResources;

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

	public Article() {
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ContentNode getContentNode() {
		return contentNode;
	}

	public Set<String> getPluginCSSResources() {
		return pluginCSSResources;
	}

	public void setPluginCSSResources(Set<String> pluginCSSResources) {
		this.pluginCSSResources = pluginCSSResources;
	}

	public Set<String> getPluginJSResources() {
		return pluginJSResources;
	}

	public void setPluginJSResources(Set<String> pluginJSResources) {
		this.pluginJSResources = pluginJSResources;
	}

	public void setContentNode(ContentNode contentNode) {
		this.contentNode = contentNode;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getOutputHTML() {
		return outputHTML;
	}

	public void setOutputHTML(String outputHTML) {
		this.outputHTML = outputHTML;
	}

}
