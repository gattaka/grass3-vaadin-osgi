package cz.gattserver.grass3.articles.interfaces;

import java.util.Set;

public class ArticleTO extends ArticleRESTTO {

	/**
	 * Obsah článku
	 */
	private String text;

	/**
	 * Obsah článku upravený pro vyhledávání
	 */
	private String searchableOutput;

	/**
	 * Dodatečné JS kódy, které je potřeba nahrát (JS z článků)
	 */
	private Set<String> pluginJSCodes;

	public ArticleTO() {
	}

	public ArticleTO(String text) {
		this.text = text;
	}

	public Set<String> getPluginJSCodes() {
		return pluginJSCodes;
	}

	public void setPluginJSCodes(Set<String> pluginJSCodes) {
		this.pluginJSCodes = pluginJSCodes;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSearchableOutput() {
		return searchableOutput;
	}

	public void setSearchableOutput(String searchableOutput) {
		this.searchableOutput = searchableOutput;
	}

}
