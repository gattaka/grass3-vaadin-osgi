package cz.gattserver.grass3.articles.latex.server;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import javax.servlet.annotation.WebServlet;

import cz.gattserver.grass3.articles.latex.config.LatexConfiguration;
import cz.gattserver.grass3.server.AbstractConfiguratedPathRequestHandler;

@WebServlet(urlPatterns = "/" + LatexConfiguration.IMAGE_PATH_ALIAS + "/*")
public class LatexImageRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Override
	protected Path getPath(String fileName) throws FileNotFoundException {
		LatexConfiguration configuration = new LatexConfiguration();
		getConfigurationService().loadConfiguration(configuration);
		return getFileSystemService().getFileSystem().getPath(configuration.getOutputPath(), fileName);
	}

}
