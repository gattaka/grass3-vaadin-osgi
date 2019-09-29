package cz.gattserver.grass3.fm;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import javax.servlet.annotation.WebServlet;

import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.server.AbstractConfiguratedPathRequestHandler;

@WebServlet(urlPatterns = "/" + FMConfiguration.FM_PATH + "/*")
public class FMRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Override
	protected Path getPath(String fileName) throws FileNotFoundException {
		FMConfiguration configuration = new FMConfiguration();
		getConfigurationService().loadConfiguration(configuration);
		return getFileSystemService().getFileSystem().getPath(configuration.getRootDir(), fileName);
	}

}
