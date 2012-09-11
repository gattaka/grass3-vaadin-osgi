package org.myftp.gattserver.grass3.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.myftp.gattserver.grass3.GrassApplication;

public class URLTool {

	/**
	 * Získá URL okna dle jeho jména
	 * 
	 * @param name
	 * @return
	 */
	public static URL getWindowURL(String name) {
		URL url = null;
		try {
			url = new URL(getApplicationURL(), name + "/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

	/**
	 * Získá URL aplikace
	 * 
	 * @return
	 */
	public static URL getApplicationURL() {
		GrassApplication application = GrassApplication.getInstance();
		return application == null ? null : application.getURL();
	}

}
