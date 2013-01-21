package org.myftp.gattserver.grass3.articles.favlink.config;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class FavlinkConfiguration {

	@XmlTransient
	public static final String CONFIG_PATH = "favicon_link_editor_plugin.xml";

	@XmlTransient
	public static final String IMAGE_PATH_ALIAS = "/articles-favlink-plugin";

	private String outputPath = "favlink/cache";

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

}
