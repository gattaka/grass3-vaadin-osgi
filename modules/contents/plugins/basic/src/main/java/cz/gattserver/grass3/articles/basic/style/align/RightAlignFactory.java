package cz.gattserver.grass3.articles.basic.style.align;

import java.util.List;

import cz.gattserver.grass3.articles.basic.style.StyleElement;
import cz.gattserver.grass3.articles.basic.style.StyleFactory;
import cz.gattserver.grass3.articles.basic.style.StyleTree;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;


/**
 * 
 * @author gatt
 */
public class RightAlignFactory extends StyleFactory {

	public final static String TAG = "ALGNRT";

	public RightAlignFactory() {
		super(TAG);
	}

	public AbstractParserPlugin getPluginParser() {
		return new StyleElement(TAG) {

			@Override
			protected StyleTree getTree(List<AbstractElementTree> elist) {
				return new RightAlignTree(elist);
			}
		};
	}

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(TAG);
		resources.setImageName("articles/basic/img/algnr_16.png");
		resources.setDescription("");
		resources.setTagFamily("Zarovnání");
		return resources;
	}

}
