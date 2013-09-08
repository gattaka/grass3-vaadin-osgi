package org.myftp.gattserver.grass3.pg.config;

import org.myftp.gattserver.grass3.config.AbstractConfiguration;

public class PhotogalleryConfiguration extends AbstractConfiguration {

	private String rootDir = "rootDir";

	public PhotogalleryConfiguration() {
		super("org.myftp.gattserver.grass3.pg");
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

}
