package cz.gattserver.grass3.articles.plugins.basic.style.align;

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
public class CenterAlignPlugin extends AbstractStylePlugin {

	public static final String TAG = "ALGNCT";

	public CenterAlignPlugin() {
		super(TAG);
	}

	@Override
	public Parser getParser() {
		return new AbstractStyleParser(TAG) {

			@Override
			protected AbstractStyleElement getElement(List<Element> elist) {
				return new CenterAlignElement(elist);
			}
		};
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "Zarovnání")
				.setImageAsThemeResource("articles/basic/img/algnc_16.png").build();
	}

}
