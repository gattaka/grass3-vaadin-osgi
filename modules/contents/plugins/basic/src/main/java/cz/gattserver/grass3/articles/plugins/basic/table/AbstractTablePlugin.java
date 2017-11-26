package cz.gattserver.grass3.articles.plugins.basic.table;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * 
 * @author gatt
 */
public abstract class AbstractTablePlugin implements Plugin {

	private final String withHeadTag = "HTABLE";
	private final String withHeadImage = "articles/basic/img/htbl_16.png";
	private final String withoutHeadTag = "TABLE";
	private final String withoutHeadImage = "articles/basic/img/tbl_16.png";

	private boolean withHead;

	private String tag;
	private String image;

	public AbstractTablePlugin(boolean withHead) {
		this.withHead = withHead;
		if (withHead) {
			tag = withHeadTag;
			image = withHeadImage;
		} else {
			tag = withoutHeadTag;
			image = withoutHeadImage;
		}
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new TableParser(tag, withHead);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(tag, "HTML").setImageAsThemeResource(image).build();
	}

}
