package org.myftp.gattserver.grass3.articles.editor.api;

/**
 * Třída obsahující všechny potřebné informace pro začlenění pluginu do UI
 * nabídky elementů v editoru
 * 
 * @author gatt
 */
public class EditorButtonResources {

	private String description;
	private String prefix;
	private String suffix;
	private String image;

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
	 */
	public EditorButtonResources(String description, String prefix, String suffix, String image) {
		this.description = description;
		this.prefix = prefix;
		this.suffix = suffix;
		this.image = image;
	}

	/**
	 * Konstruktor pro případy "běžných" prvků, kdy je všechno stejné - jak
	 * popisek, tak počteční a koncový tag. Z logiky věci vyplývá, že zadávaný
	 * parametr je pouze název elementu/tagu bez hranatých závorek nebo lomítek
	 * 
	 * @param tag
	 */
	public EditorButtonResources(String tag) {
		this.description = tag;
		this.prefix = '[' + tag + ']';
		this.suffix = "[/" + tag + ']';
		this.image = "";
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

}
