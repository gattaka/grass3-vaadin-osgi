package cz.gattserver.grass3.print3d;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import javax.servlet.annotation.WebServlet;

import cz.gattserver.grass3.print3d.config.Print3dConfiguration;
import cz.gattserver.grass3.server.AbstractConfiguratedPathRequestHandler;

@WebServlet(urlPatterns = "/" + Print3dConfiguration.PRINT3D_PATH + "/*")
public class Print3dRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Override
	protected Path getPath(String fileName) throws FileNotFoundException {
		Print3dConfiguration configuration = new Print3dConfiguration();
		getConfigurationService().loadConfiguration(configuration);
		return getFileSystemService().getFileSystem().getPath(configuration.getRootDir(), fileName);
	}

}
