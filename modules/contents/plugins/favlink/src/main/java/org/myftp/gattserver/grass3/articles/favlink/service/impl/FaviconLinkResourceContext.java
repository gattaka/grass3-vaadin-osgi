package org.myftp.gattserver.grass3.articles.favlink.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;

public class FaviconLinkResourceContext implements HttpContext {

	public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return true;
	}

	public URL getResource(String name) {
		try {
			return new File(name).toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getMimeType(String name) {
		return "image/png png";
	}

}
