package cz.gattserver.grass3.articles.plugins.favlink.plugin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;
import cz.gattserver.grass3.articles.plugins.favlink.FallbackFaviconObtainStrategy;
import cz.gattserver.grass3.articles.plugins.favlink.AddressFaviconObtainStrategy;
import cz.gattserver.grass3.articles.plugins.favlink.HeaderFaviconObtainStrategy;
import cz.gattserver.grass3.articles.plugins.favlink.FaviconObtainStrategy;
import cz.gattserver.web.common.ui.ImageIcon;

/**
 * @author gatt
 */
@Component
public class FavlinkPlugin implements Plugin {

	private final String tag = "A";

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		List<FaviconObtainStrategy> strategies = new ArrayList<>();
		strategies.add(new AddressFaviconObtainStrategy());
		strategies.add(new HeaderFaviconObtainStrategy());
		strategies.add(new FallbackFaviconObtainStrategy());
		return new FavlinkParser(tag, strategies);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(tag, "HTML").setDescription("Link")
				.setImageResource(ImageIcon.GLOBE_16_ICON.createResource()).build();
	}
}
