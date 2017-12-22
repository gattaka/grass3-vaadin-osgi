package cz.gattserver.grass3.articles.latex.config;

import cz.gattserver.grass3.config.AbstractConfiguration;

public class LatexConfiguration extends AbstractConfiguration {

	public static final String IMAGE_PATH_ALIAS = "articles-latex-plugin";

	private String outputPath = "latex/output";

	public LatexConfiguration() {
		super("cz.gattserver.grass3.articles.latex");
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

}
