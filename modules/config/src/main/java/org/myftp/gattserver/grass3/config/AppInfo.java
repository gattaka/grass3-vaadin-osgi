package org.myftp.gattserver.grass3.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppInfo {

	private static AppInfo instance;
	private static final String propertiesPath = "/bundle.properties";
	public static final String GRASS_NAME = "grass";
	private String grassVersion;
	
	private AppInfo() {
//		Properties  prop = new Properties();
//		InputStream in = AppInfo.class.getResourceAsStream(propertiesPath);
//		try {
//			prop.load(in);
//			grassVersion = prop.getProperty("version");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		// TODO ... quickfix ... co se tady sakra stalo - getResourceAsStream vrací null ?!
		grassVersion = "0.0.1-SNAPSHOT";
	}

	public static AppInfo getInstance() {
		if (instance == null)
			instance = new AppInfo();
		return instance;
	}

	public String getGrassVersion() {
		return grassVersion;
	}
}
