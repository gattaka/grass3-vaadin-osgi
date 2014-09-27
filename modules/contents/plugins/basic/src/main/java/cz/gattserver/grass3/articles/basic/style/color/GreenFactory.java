package cz.gattserver.grass3.articles.basic.style.color;

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
public class GreenFactory extends StyleFactory {

	public final static String TAG = "GRN";

	public GreenFactory() {
		super(TAG);
	}

	public AbstractParserPlugin getPluginParser() {
		return new StyleElement(TAG) {

			@Override
			protected StyleTree getTree(List<AbstractElementTree> elist) {
				return new GreenTree(elist);
			}
		};
	}

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(TAG);
		resources.setDescription("Zeleně");
		resources.setTagFamily("Obarvení");
		return resources;
	}

}
