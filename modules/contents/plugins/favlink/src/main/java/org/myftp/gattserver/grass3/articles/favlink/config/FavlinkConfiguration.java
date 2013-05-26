package org.myftp.gattserver.grass3.articles.favlink.config;

import org.myftp.gattserver.grass3.config.AbstractConfiguration;

public class FavlinkConfiguration extends AbstractConfiguration {

	public FavlinkConfiguration() {
		super("org.myftp.gattserver.grass3.articles.favlink");
	}

	public static final String IMAGE_PATH_ALIAS = "/articles-favlink-plugin";

	private String outputPath = "favlink/cache";

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

}
