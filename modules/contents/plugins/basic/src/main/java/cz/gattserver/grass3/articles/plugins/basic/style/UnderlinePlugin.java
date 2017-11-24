package cz.gattserver.grass3.articles.plugins.basic.style;

import java.util.List;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

/**
 * @author gatt
 */
@Component
public class UnderlinePlugin extends AbstractStylePlugin {

	public final static String TAG = "UND";

	public UnderlinePlugin() {
		super(TAG);
	}

	@Override
	public Parser getParser() {
		return new AbstractStyleParser(TAG) {

			@Override
			protected AbstractStyleElement getElement(List<Element> elist) {
				return new UnderlineElement(elist);
			}
		};
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		EditorButtonResourcesTO resources = new EditorButtonResourcesTO(TAG);
		resources.setImageName("articles/basic/img/und_16.png");
		resources.setDescription("");
		resources.setTagFamily("Formátování");
		return resources;
	}

}
