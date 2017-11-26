package cz.gattserver.grass3.articles.plugins.basic.style;

import java.util.List;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;

/**
 * @author gatt
 */
@Component
public class SupPlugin extends AbstractStylePlugin {

	public final static String TAG = "SUP";

	public SupPlugin() {
		super(TAG);
	}

	@Override
	public Parser getParser() {
		return new AbstractStyleParser(TAG) {

			@Override
			protected AbstractStyleElement getElement(List<Element> elist) {
				return new SupElement(elist);
			}
		};
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "Formátování").setDescription("Sup").build();
	}

}
