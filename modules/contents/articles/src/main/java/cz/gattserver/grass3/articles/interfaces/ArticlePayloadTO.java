package cz.gattserver.grass3.articles.interfaces;

import java.util.Collection;

/**
 * TO objekt pro přenos základních obsahových informací o článku
 * 
 * @author Hynek
 *
 */
public class ArticlePayloadTO {

	private String name;
	private String text;
	private Collection<String> tags;
	private boolean publicated;
	private String contextRoot;

	/**
	 * Výchozí konstruktor
	 */
	public ArticlePayloadTO() {
	}

	/**
	 * @param name
	 *            název článku
	 * @param text
	 *            obsah článku
	 * @param tags
	 *            klíčová slova
	 * @param publicated
	 *            <code>true</code>, pokud má být článek zveřejněn
	 * @param contextRoot
	 *            kořenová adresa, od které mají být vytvoření linky na CSS a JS
	 *            zdroje, jež může článek na sobě mít
	 */
	public ArticlePayloadTO(String name, String text, Collection<String> tags, boolean publicated, String contextRoot) {
		this.name = name;
		this.text = text;
		this.tags = tags;
		this.publicated = publicated;
		this.contextRoot = contextRoot;
	}

	/**
	 * @return kořenová adresa, od které mají být vytvoření linky na CSS a JS
	 *         zdroje, jež může článek na sobě mít
	 */
	public String getContextRoot() {
		return contextRoot;
	}

	/**
	 * @param contextRoot
	 *            kořenová adresa, od které mají být vytvoření linky na CSS a JS
	 *            zdroje, jež může článek na sobě mít
	 * @return tento objekt pro řetězení
	 */
	public ArticlePayloadTO setContextRoot(String contextRoot) {
		this.contextRoot = contextRoot;
		return this;
	}

	/**
	 * @return název článku
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            název článku
	 * @return tento objekt pro řetězení
	 */
	public ArticlePayloadTO setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * @return obsah článku
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            obsah článku
	 * @return tento objekt pro řetězení
	 */
	public ArticlePayloadTO setText(String text) {
		this.text = text;
		return this;
	}

	/**
	 * @return klíčová slova
	 */
	public Collection<String> getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            klíčová slova
	 * @return tento objekt pro řetězení
	 */
	public ArticlePayloadTO setTags(Collection<String> tags) {
		this.tags = tags;
		return this;
	}

	/**
	 * @return <code>true</code>, pokud má být článek zveřejněn
	 */
	public boolean isPublicated() {
		return publicated;
	}

	/**
	 * @param publicated
	 *            <code>true</code>, pokud má být článek zveřejněn
	 * @return tento objekt pro řetězení
	 */
	public ArticlePayloadTO setPublicated(boolean publicated) {
		this.publicated = publicated;
		return this;
	}

}
