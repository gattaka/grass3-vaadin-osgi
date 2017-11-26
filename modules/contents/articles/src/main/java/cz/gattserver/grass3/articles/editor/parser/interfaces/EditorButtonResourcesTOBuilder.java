package cz.gattserver.grass3.articles.editor.parser.interfaces;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

/**
 * Builder pro immutable {@link EditorButtonResourcesTO}
 * 
 * @author Hynek
 *
 */
public class EditorButtonResourcesTOBuilder {

	private String tag;
	private String tagFamily;
	private String description;
	private String prefix;
	private String suffix;
	private Resource imageResource;

	/**
	 * @param tag
	 *            název tagu, použitý v tagové značce
	 * @param tagFamily
	 *            rodina tagů, do které bude ve výběru začleněn
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
	public EditorButtonResourcesTOBuilder(String tag, String tagFamily, String description, String prefix,
			String suffix, Resource imageResource) {
		this.tag = tag;
		this.tagFamily = tagFamily;
		this.description = description;
		this.prefix = prefix;
		this.suffix = suffix;
		this.imageResource = imageResource;
	}

	/**
	 * Konstruktor pro případy "běžných" prvků, kdy je všechno stejné - jak
	 * popisek, tak počteční a koncový tag. Z logiky věci vyplývá, že zadávaný
	 * parametr je pouze název elementu/tagu bez hranatých závorek nebo lomítek
	 * 
	 * @param tag
	 *            název tagu, použitý v tagové značce
	 * @param tagFamily
	 *            rodina tagů, do které bude ve výběru začleněn
	 */
	public EditorButtonResourcesTOBuilder(String tag, String tagFamily) {
		this.tag = tag;
		this.tagFamily = tagFamily;
		this.imageResource = null;
	}

	public EditorButtonResourcesTO build() {
		Validate.notBlank(tag);
		String prefix = this.prefix;
		String suffix = this.suffix;
		String defaultPrefix = '[' + tag + ']';
		String defaultSuffix = "[/" + tag + ']';
		if (StringUtils.isBlank(prefix))
			prefix = defaultPrefix;
		if (StringUtils.isBlank(suffix))
			suffix = defaultSuffix;

		if (!prefix.startsWith(defaultPrefix))
			throw new IllegalArgumentException("Prefix musí začínat: " + defaultPrefix);
		if (!suffix.endsWith(defaultSuffix))
			throw new IllegalArgumentException("Suffix musí končit: " + defaultSuffix);

		return new EditorButtonResourcesTO(tag, tagFamily, description, prefix, suffix, imageResource);
	}

	public String getTag() {
		return tag;
	}

	public EditorButtonResourcesTOBuilder setTag(String tag) {
		this.tag = tag;
		return this;
	}

	public String getTagFamily() {
		return tagFamily;
	}

	public EditorButtonResourcesTOBuilder setTagFamily(String tagFamily) {
		this.tagFamily = tagFamily;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public EditorButtonResourcesTOBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getPrefix() {
		return prefix;
	}

	public EditorButtonResourcesTOBuilder setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public String getSuffix() {
		return suffix;
	}

	public EditorButtonResourcesTOBuilder setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}

	public Resource getImageResource() {
		return imageResource;
	}

	public EditorButtonResourcesTOBuilder setImageAsThemeResource(String image) {
		this.imageResource = new ThemeResource(image);
		return this;
	}

	public EditorButtonResourcesTOBuilder setImageResource(Resource imageResource) {
		this.imageResource = imageResource;
		return this;
	}

}
