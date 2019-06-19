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
	private static final String TAG_STARTX = "STARTX";
	private static final String TAG_ENDX = "ENDX";
	private static final String TAG_STARTY = "STARTY";
	private static final String TAG_ENDY = "ENDY";
	private static final String TAG_FUNC = "FUNC";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new PlotterParser(TAG, TAG_STARTX, TAG_ENDX, TAG_STARTY, TAG_ENDY, TAG_FUNC);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "Šablony").setPrefix(
				"[" + TAG + "][" + TAG_STARTX + "][/" + TAG_STARTX + "][" + TAG_ENDX + "][/" + TAG_ENDX + "]["
						+ TAG_STARTY + "][/" + TAG_STARTY + "][" + TAG_ENDY + "][/" + TAG_ENDY + "][" + TAG_FUNC + "]")
				.setSuffix("[/" + TAG_FUNC + "][/" + TAG + "]").setDescription("Plotter").build();
	}
}
