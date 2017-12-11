package cz.gattserver.grass3.articles.plugins.favlink.strategies;

import cz.gattserver.web.common.ui.ImageIcon;

/**
 * Poslední možnost jak obsadit obrázek odkazu -- výchozí univerzální ikona.
 * Nezjišťuje nic, natvrdo vrací vždy stejnou ikonu.
 * 
 * @author Hynek
 *
 */
public class FallbackFaviconObtainStrategy implements FaviconObtainStrategy {

	private static final String DEFAULT_FAVICON = "/" + ImageIcon.GLOBE_16_ICON.name();

	@Override
	public String obtainFaviconURL(String pageURL, String contextRoot) {
		return DEFAULT_FAVICON;
	}

}
