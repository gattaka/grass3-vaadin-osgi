package cz.gattserver.grass3.articles.basic.table;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;

/**
 * 
 * @author gatt
 */
public class TableFactory implements PluginFactory {

	private final String withHeadTag = "HTABLE";
	private final String withHeadImage = "articles/basic/img/htbl_16.png";
	private final String withoutHeadTag = "TABLE";
	private final String withoutHeadImage = "articles/basic/img/tbl_16.png";

	private boolean withHead;

	private String tag;
	private String image;

	public String getTag() {
		return tag;
	}

	public AbstractParserPlugin getPluginParser() {
		return new TableElement(tag, withHead);
	}

	public TableFactory(boolean withHead) {
		this.withHead = withHead;
		if (withHead) {
			tag = withHeadTag;
			image = withHeadImage;
		} else {
			tag = withoutHeadTag;
			image = withoutHeadImage;
		}

	}

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(tag);
		resources.setImageName(image);
		resources.setDescription("");
		resources.setTagFamily("HTML");
		return resources;
	}

}
