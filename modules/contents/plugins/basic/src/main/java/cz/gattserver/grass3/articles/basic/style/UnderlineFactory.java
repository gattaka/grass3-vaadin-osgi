package cz.gattserver.grass3.articles.basic.style;

import java.util.List;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;

/**
 * 
 * @author gatt
 */
public class UnderlineFactory extends StyleFactory {

	public final static String TAG = "UND";

	public UnderlineFactory() {
		super(TAG);
	}

	public AbstractParserPlugin getPluginParser() {
		return new StyleElement(TAG) {

			@Override
			protected StyleTree getTree(List<AbstractElementTree> elist) {
				return new UnderlineTree(elist);
			}
		};
	}

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(TAG);
		resources.setImageName("articles/basic/img/und_16.png");
		resources.setDescription("");
		resources.setTagFamily("Formátování");
		return resources;
	}

}
