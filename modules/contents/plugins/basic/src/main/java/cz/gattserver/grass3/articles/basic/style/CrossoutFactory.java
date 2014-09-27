package cz.gattserver.grass3.articles.basic.style;

import java.util.List;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;


/**
 * 
 * @author gatt
 */
public class CrossoutFactory extends StyleFactory {

	public final static String TAG = "CROSS";

	public CrossoutFactory() {
		super(TAG);
	}

	public AbstractParserPlugin getPluginParser() {
		return new StyleElement(TAG) {

			@Override
			protected StyleTree getTree(List<AbstractElementTree> elist) {
				return new CrossoutTree(elist);
			}
		};
	}

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(TAG);
		resources.setImageName("articles/basic/img/cross_16.png");
		resources.setDescription("");
		resources.setTagFamily("Formátování");
		return resources;
	}

}
