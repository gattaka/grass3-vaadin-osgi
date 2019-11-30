package cz.gattserver.grass3.articles.plugins.basic.style.align;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleElement;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStylePlugin;

/**
 * @author gatt
 */
public class LeftAlignPlugin extends AbstractStylePlugin {

	public static final String TAG = "ALGNLT";

	public LeftAlignPlugin() {
		super(TAG);
	}

	@Override
	public Parser getParser() {
		return new AbstractAlignParser(TAG) {

			@Override
			protected AbstractStyleElement getElement(List<Element> elist) {
				return new LeftAlignElement(elist);
			}
		};
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, AbstractStylePlugin.PLUGIN_FAMILY)
				.setImageAsThemeResource("basic/img/algnl_16.png").build();
	}

}
