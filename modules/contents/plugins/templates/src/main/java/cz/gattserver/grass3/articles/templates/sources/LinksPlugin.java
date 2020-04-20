package cz.gattserver.grass3.articles.templates.sources;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;
import cz.gattserver.grass3.articles.plugins.favlink.strategies.CombinedFaviconObtainStrategy;
import cz.gattserver.web.common.ui.ImageIcon;

/**
 * @author gatt
 */
@Component
public class LinksPlugin implements Plugin {

	private static final String TAG = "LINKS";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new SourcesParser(TAG, new CombinedFaviconObtainStrategy(), false, false);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "Šablony").setDescription("Odkazy")
				.setImageResource(ImageIcon.GLOBE_16_ICON.createResource()).build();
	}
}
