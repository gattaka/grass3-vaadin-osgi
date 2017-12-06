package cz.gattserver.grass3.articles.plugins.favlink.plugin;

import cz.gattserver.grass3.articles.plugins.favlink.FaviconObtainStrategy;

public class MockFaviconObtainStrategy implements FaviconObtainStrategy {

	@Override
	public String obtainFaviconURL(String pageURL, String contextRoot) {
		return "http://mock.neco/favicon.png";
	}

}
