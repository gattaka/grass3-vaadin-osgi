package cz.gattserver.grass3.articles.plugins.favlink.strategies;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.grass3.articles.plugins.favlink.FaviconCache;
import cz.gattserver.grass3.articles.plugins.favlink.FaviconUtils;

/**
 * Strategie kombinující ostatníc strategie v pořadí dle náročnosti.
 * 
 * @author gatt
 */
public class CombinedFaviconObtainStrategy implements FaviconObtainStrategy {

	@Override
	public String obtainFaviconURL(String pageURL, String contextRoot) {
		FaviconCache cache = new FaviconCache();
		List<FaviconObtainStrategy> strategies = new ArrayList<>();
		strategies.add(new CacheFaviconObtainStrategy(cache));
		strategies.add(new AddressFaviconObtainStrategy(cache));
		strategies.add(new HeaderFaviconObtainStrategy(cache));

		for (FaviconObtainStrategy s : strategies) {
			String faviconURL = s.obtainFaviconURL(pageURL, contextRoot);
			if (faviconURL != null)
				return faviconURL;
		}

		return FaviconUtils.createDefaultFaviconAddress(contextRoot);
	}

}
