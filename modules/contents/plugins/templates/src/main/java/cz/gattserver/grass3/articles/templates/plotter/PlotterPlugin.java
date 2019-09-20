package cz.gattserver.grass3.articles.templates.plotter;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
@Component
public class PlotterPlugin implements Plugin {

	private static final String TAG = "PLOTTER";
	private static final String IMAGE_PATH = "templates/img/plotter_16.png";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new PlotterParser(TAG);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "Šablony")
				.setPrefix("[" + TAG + "]x*x;2;5;0;0[;width][;height]").setSuffix("[/" + TAG + "]")
				.setDescription("Plotter").setImageAsThemeResource(IMAGE_PATH).build();
	}
}
