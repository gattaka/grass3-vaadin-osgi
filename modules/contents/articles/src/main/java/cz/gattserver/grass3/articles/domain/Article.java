package cz.gattserver.grass3.articles.domain;

import java.util.Set;
import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SortComparator;

import cz.gattserver.grass3.model.domain.ContentNode;

@Entity(name = "ARTICLE")
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
	 * Obsah článku upravený pro vyhledávání
	 */
	@Column(columnDefinition = "TEXT")
	private String searchableOutput;

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
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@SortComparator(ArticleJSResourceComparator.class)
	private SortedSet<ArticleJSResource> pluginJSResources;

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	/**
	 * Je-li draft a má-li rozpracovanou pouze část článku, pak kterou
	 */
	private Integer partNumber;

	public Integer getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(Integer partNumber) {
		this.partNumber = partNumber;
	}

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

	public SortedSet<ArticleJSResource> getPluginJSResources() {
		return pluginJSResources;
	}

	public void setPluginJSResources(SortedSet<ArticleJSResource> pluginJSResources) {
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

	public String getSearchableOutput() {
		return searchableOutput;
	}

	public void setSearchableOutput(String searchableOutput) {
		this.searchableOutput = searchableOutput;
	}

}
