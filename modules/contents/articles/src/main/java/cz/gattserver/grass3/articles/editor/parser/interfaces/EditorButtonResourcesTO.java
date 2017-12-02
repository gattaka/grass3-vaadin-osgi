package cz.gattserver.grass3.articles.editor.parser.interfaces;

import com.vaadin.server.Resource;

/**
 * Třída obsahující všechny potřebné informace pro začlenění pluginu do UI
 * nabídky elementů v editoru
 * 
 * @author gatt
 */
public class EditorButtonResourcesTO implements Comparable<EditorButtonResourcesTO> {

	private String tag;
	private String tagFamily;
	private String description;
	private String prefix;
	private String suffix;
	private Resource imageResource;

	protected EditorButtonResourcesTO(String tag, String tagFamily, String description, String prefix, String suffix,
			Resource imageResource) {
		this.tag = tag;
		this.tagFamily = tagFamily;
		this.description = description;
		this.prefix = prefix;
		this.suffix = suffix;
		this.imageResource = imageResource;
	}

	/**
	 * Získá popisek elementu
	 * 
	 * @return popisek
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Vkládaný text před označenou část článku
	 * 
	 * @return vkládaný text
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Vkládaný text za označenou část článku
	 * 
	 * @return vkládaný text
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * Získá název rodiny elementů, pod kterou má být seskupen plugin v editoru
	 * 
	 * @return název rodiny pluginů - např. LaTeX, HTML, FancyNadpisy apod.
	 */
	public String getTagFamily() {
		return tagFamily;
	}

	/**
	 * Získá zdroj pro obrázek tlačítka
	 * 
	 * @return resource obrázku
	 */
	public Resource getImage() {
		return imageResource;
	}

	/**
	 * Získá tag
	 * 
	 * @return název tagu
	 */
	public String getTag() {
		return tag;
	}

	public int compareTo(EditorButtonResourcesTO o) {
		return tag.compareTo(o.getTag());
	}

	@Override
	public int hashCode() {
		return tag.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EditorButtonResourcesTO) {
			return tag.equals(((EditorButtonResourcesTO) obj).getTag());
		} else
			return false;
	}

}
