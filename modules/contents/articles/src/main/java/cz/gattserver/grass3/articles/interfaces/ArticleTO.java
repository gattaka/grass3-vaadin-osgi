package cz.gattserver.grass3.articles.interfaces;

import java.util.Set;

import cz.gattserver.grass3.interfaces.ContentNodeTO;

public class ArticleTO {

	/**
	 * Obsah článku
	 */
	private String text;

	/**
	 * Přeložený obsah článku
	 */
	private String outputHTML;

	/**
	 * Obsah článku upravený pro vyhledávání
	 */
	private String searchableOutput;

	/**
	 * Meta-informace o obsahu
	 */
	private ContentNodeTO contentNode;

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Dodatečné CSS resources, které je potřeba nahrát (od pluginů)
	 */
	private Set<String> pluginCSSResources;

	/**
	 * Dodatečné JS resources, které je potřeba nahrát (od pluginů)
	 */
	private Set<String> pluginJSResources;

	public ArticleTO() {
	}

	public ArticleTO(String text) {
		this.text = text;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ContentNodeTO getContentNode() {
		return contentNode;
	}

	public void setContentNode(ContentNodeTO contentNode) {
		this.contentNode = contentNode;
	}

	public String getOutputHTML() {
		return outputHTML;
	}

	public void setOutputHTML(String outputHTML) {
		this.outputHTML = outputHTML;
	}

	public String getSearchableOutput() {
		return searchableOutput;
	}

	public void setSearchableOutput(String searchableOutput) {
		this.searchableOutput = searchableOutput;
	}

}