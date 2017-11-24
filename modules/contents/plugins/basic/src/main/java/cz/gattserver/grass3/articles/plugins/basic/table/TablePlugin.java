package cz.gattserver.grass3.articles.plugins.basic.table;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * 
 * @author gatt
 */
@Component
public class TablePlugin implements Plugin {

	private final String withHeadTag = "HTABLE";
	private final String withHeadImage = "articles/basic/img/htbl_16.png";
	private final String withoutHeadTag = "TABLE";
	private final String withoutHeadImage = "articles/basic/img/tbl_16.png";

	private boolean withHead;

	private String tag;
	private String image;

	public TablePlugin(boolean withHead) {
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
		EditorButtonResourcesTO resources = new EditorButtonResourcesTO(tag);
		resources.setImageName(image);
		resources.setDescription("");
		resources.setTagFamily("HTML");
		return resources;
	}

}
