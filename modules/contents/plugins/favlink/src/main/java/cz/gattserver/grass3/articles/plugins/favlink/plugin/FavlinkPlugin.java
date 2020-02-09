package cz.gattserver.grass3.articles.plugins.favlink.plugin;

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
public class FavlinkPlugin implements Plugin {

	private static final String TAG = "A";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new FavlinkParser(TAG, new CombinedFaviconObtainStrategy());
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "HTML").setDescription("Link")
				.setImageResource(ImageIcon.GLOBE_16_ICON.createResource()).build();
	}
}
