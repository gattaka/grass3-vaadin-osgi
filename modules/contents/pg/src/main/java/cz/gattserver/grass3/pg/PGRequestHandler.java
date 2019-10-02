package cz.gattserver.grass3.pg;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import javax.servlet.annotation.WebServlet;

import cz.gattserver.grass3.pg.config.PGConfiguration;
import cz.gattserver.grass3.server.AbstractConfiguratedPathRequestHandler;

@WebServlet(urlPatterns = "/" + PGConfiguration.PG_PATH + "/*")
public class PGRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Override
	protected Path getPath(String fileName) throws FileNotFoundException {
		PGConfiguration configuration = new PGConfiguration();
		getConfigurationService().loadConfiguration(configuration);
		return getFileSystemService().getFileSystem().getPath(configuration.getRootDir(), fileName);
	}

}
