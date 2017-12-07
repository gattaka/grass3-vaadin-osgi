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

	private final static String defaultFavicon = "/" + ImageIcon.LABEL_16_ICON;

	@Override
	public String obtainFaviconURL(String pageURL, String contextRoot) {
		return defaultFavicon;
	}

}
