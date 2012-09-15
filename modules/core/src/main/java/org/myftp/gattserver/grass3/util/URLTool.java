package org.myftp.gattserver.grass3.util;

import java.net.MalformedURLException;
import java.net.URL;

public class URLTool {

	/**
	 * Získá URL okna dle jeho jména
	 * 
	 * @param name
	 * @return
	 */
	public static URL getWindowURL(URL appURL, String name) {
		URL url = null;
		try {
			url = new URL(appURL, name + "/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

}
