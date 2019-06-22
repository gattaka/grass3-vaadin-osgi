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
		return new EditorButtonResourcesTOBuilder(TAG, "Šablony").setPrefix("[" + TAG + "]x*x;-5;5;-10;10[;width][;height]")
				.setSuffix("[/" + TAG + "]").setDescription("Plotter").build();
	}
}
