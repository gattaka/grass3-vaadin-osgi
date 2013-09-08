package org.myftp.gattserver.grass3.pg.config;

import org.myftp.gattserver.grass3.config.AbstractConfiguration;

public class PhotogalleryConfiguration extends AbstractConfiguration {

	private String rootDir = "rootDir";

	private String miniaturesDir = "foto_mini";

	public PhotogalleryConfiguration() {
		super("org.myftp.gattserver.grass3.pg");
	}

	public String getMiniaturesDir() {
		return miniaturesDir;
	}

	public void setMiniaturesDir(String miniaturesDir) {
		this.miniaturesDir = miniaturesDir;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

}
