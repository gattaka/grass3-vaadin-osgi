package org.myftp.gattserver.grass3.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppInfo {

	private static Properties prop;
	static {
		String path = "/bundle.properties";
		prop = new Properties();
		InputStream in = AppInfo.class.getResourceAsStream(path);
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final String GRASS_NAME = "grass";
	public static final String FM_MODULE_CONFIG_PATH = "file_manager.xml";

	public static final String GRASS_VERSION = prop.getProperty("version");

	private AppInfo() {
	}

}
