package cz.gattserver.grass3.articles.plugins.favlink.plugin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;
import cz.gattserver.grass3.articles.plugins.favlink.strategies.AddressFaviconObtainStrategy;
import cz.gattserver.grass3.articles.plugins.favlink.strategies.FallbackFaviconObtainStrategy;
import cz.gattserver.grass3.articles.plugins.favlink.strategies.FaviconObtainStrategy;
import cz.gattserver.grass3.articles.plugins.favlink.strategies.HeaderFaviconObtainStrategy;
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
		List<FaviconObtainStrategy> strategies = new ArrayList<>();
		strategies.add(new AddressFaviconObtainStrategy());
		strategies.add(new HeaderFaviconObtainStrategy());
		strategies.add(new FallbackFaviconObtainStrategy());
		return new FavlinkParser(TAG, strategies);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "HTML").setDescription("Link")
				.setImageResource(ImageIcon.GLOBE_16_ICON.createResource()).build();
	}
}
