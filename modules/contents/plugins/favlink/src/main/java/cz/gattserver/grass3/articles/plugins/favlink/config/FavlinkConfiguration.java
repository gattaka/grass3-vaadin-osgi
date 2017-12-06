package cz.gattserver.grass3.articles.plugins.favlink.config;

import cz.gattserver.grass3.config.AbstractConfiguration;

public class FavlinkConfiguration extends AbstractConfiguration {

	public FavlinkConfiguration() {
		super("cz.gattserver.grass3.articles.favlink");
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
