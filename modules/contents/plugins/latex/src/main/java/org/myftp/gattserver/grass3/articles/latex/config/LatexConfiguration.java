package org.myftp.gattserver.grass3.articles.latex.config;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class LatexConfiguration {

	@XmlTransient
	public static final String CONFIG_PATH = "latex_editor_plugin.xml";
	
	@XmlTransient
	public static final String IMAGE_PATH_ALIAS = "/articles-latex-plugin";

	private String outputPath = "latex/output";

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

}
