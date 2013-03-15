package org.myftp.gattserver.grass3.articles.editor.api;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;


/**
 * Třída obsahující všechny potřebné informace pro začlenění pluginu do UI
 * nabídky elementů v editoru
 * 
 * @author gatt
 */
public class EditorButtonResources implements Comparable<EditorButtonResources> {

	private String tag;
	private String tagFamily;
	private String description;
	private String prefix;
	private String suffix;
	private Resource imageResource;

	/**
	 * Default constructor
	 * 
	 * @param description
	 *            nápis na vkládacím prvku v editoru (popisek tlačítka)
	 * @param prefix
	 *            počáteční tag + (nepovinné) nějaké věci, které se mají vložit
	 *            před označený text
	 * @param suffix
	 *            koncový tag + (nepovinné) nějaké věci, které se mají vložit za
	 *            označený text
	 * @param imageResource
	 *            resource ikony pluginu
	 */
	public EditorButtonResources(String tag, String description, String prefix,
			String suffix, Resource imageResource) {
		this.tag = tag;
		this.description = description;
		this.prefix = prefix;
		this.suffix = suffix;
		this.imageResource = imageResource;
	}

	/**
	 * Default constructor
	 * 
	 * @param description
	 *            nápis na vkládacím prvku v editoru (popisek tlačítka)
	 * @param prefix
	 *            počáteční tag + (nepovinné) nějaké věci, které se mají vložit
	 *            před označený text
	 * @param suffix
	 *            koncový tag + (nepovinné) nějaké věci, které se mají vložit za
	 *            označený text
	 * @param imageName
	 *            název ikony pluginu (bude vzata z Theme resources)
	 */
	public EditorButtonResources(String tag, String description, String prefix,
			String suffix, String imageName) {
		this.tag = tag;
		this.description = description;
		this.prefix = prefix;
		this.suffix = suffix;
		this.imageResource = new ThemeResource(imageName);
	}

	/**
	 * Konstruktor pro případy "běžných" prvků, kdy je všechno stejné - jak
	 * popisek, tak počteční a koncový tag. Z logiky věci vyplývá, že zadávaný
	 * parametr je pouze název elementu/tagu bez hranatých závorek nebo lomítek
	 * 
	 * @param tag
	 */
	public EditorButtonResources(String tag) {
		this.tag = tag;
		this.description = tag;
		this.prefix = '[' + tag + ']';
		this.suffix = "[/" + tag + ']';
		this.imageResource = null;
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

	public void setTagFamily(String tagFamily) {
		this.tagFamily = tagFamily;
	}

	public Resource getImage() {
		return imageResource;
	}

	public void setImage(Resource image) {
		this.imageResource = image;
	}

	public void setImageName(String imageName) {
		this.imageResource = new ThemeResource(imageName);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int compareTo(EditorButtonResources o) {
		return tag.compareTo(o.getTag());
	}

	@Override
	public int hashCode() {
		return tag.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EditorButtonResources) {
			return tag.equals(((EditorButtonResources) obj).getTag());
		} else
			return false;
	}

}
