package cz.gattserver.grass3.articles.plugins.basic.style.color;

import java.util.List;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleElement;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleParser;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStylePlugin;

/**
 * @author gatt
 */
@Component
public class GreenPlugin extends AbstractStylePlugin {

	public static final String TAG = "GRN";

	public GreenPlugin() {
		super(TAG);
	}

	@Override
	public Parser getParser() {
		return new AbstractStyleParser(TAG) {

			@Override
			protected AbstractStyleElement getElement(List<Element> elist) {
				return new GreenElement(elist);
			}
		};
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, AbstractStylePlugin.PLUGIN_FAMILY).setDescription("Zeleně")
				.build();
	}

}
