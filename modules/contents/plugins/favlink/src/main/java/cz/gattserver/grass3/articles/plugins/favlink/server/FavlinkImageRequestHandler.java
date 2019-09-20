package cz.gattserver.grass3.articles.plugins.favlink.server;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import javax.servlet.annotation.WebServlet;

import cz.gattserver.grass3.articles.plugins.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass3.server.AbstractConfiguratedPathRequestHandler;

@WebServlet(urlPatterns = "/" + FavlinkConfiguration.IMAGE_PATH_ALIAS + "/*")
public class FavlinkImageRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Override
	protected Path getPath(String fileName) throws FileNotFoundException {
		FavlinkConfiguration configuration = new FavlinkConfiguration();
		getConfigurationService().loadConfiguration(configuration);
		return getFileSystemService().getFileSystem().getPath(configuration.getOutputPath(), fileName);
	}

}
