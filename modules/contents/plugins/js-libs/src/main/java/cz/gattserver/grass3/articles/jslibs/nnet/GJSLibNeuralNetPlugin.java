package cz.gattserver.grass3.articles.jslibs.nnet;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.jslibs.GJSLibParser;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
@Component
public class GJSLibNeuralNetPlugin implements Plugin {

	private static final String TAG = "GJSLibNeuralNet";
	private static final String IMAGE_PATH = "jslibs/img/nnet_16.png";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new GJSLibParser(TAG) {
			@Override
			protected Element createElement() {
				return new GJSLibNeuralNetElement();
			}
		};
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "JS Libs").setPrefix("[" + TAG + "]").setSuffix("[/" + TAG + "]")
				.setDescription("NNet").setImageAsThemeResource(IMAGE_PATH).build();
	}
}
