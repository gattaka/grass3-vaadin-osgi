package cz.gattserver.grass3.articles.plugins.basic.style.color;

import java.util.List;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleElement;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleParser;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStylePlugin;

/**
 *
 * @author gatt
 */
@Component
public class RedPlugin extends AbstractStylePlugin {

	public final static String TAG = "RED";

	public RedPlugin() {
		super(TAG);
	}

	@Override
	public Parser getParser() {
		return new AbstractStyleParser(TAG) {

			@Override
			protected AbstractStyleElement getElement(List<Element> elist) {
				return new RedElement(elist);
			}
		};
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		EditorButtonResourcesTO resources = new EditorButtonResourcesTO(TAG);
		resources.setDescription("Červeně");
		resources.setTagFamily("Obarvení");
		return resources;
	}

}
