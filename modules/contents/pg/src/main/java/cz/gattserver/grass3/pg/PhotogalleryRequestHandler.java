package cz.gattserver.grass3.pg;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pg.config.PGConfiguration;
import cz.gattserver.grass3.server.AbstractConfiguratedPathRequestHandler;

@Component("pgRequestHandler")
public class PhotogalleryRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	public PhotogalleryRequestHandler() {
		super(PGConfiguration.PHOTOGALLERY_PATH);
	}

	@Override
	protected Path getPath(String fileName) throws FileNotFoundException {
		PGConfiguration configuration = new PGConfiguration();
		getConfigurationService().loadConfiguration(configuration);
		return getFileSystemService().getFileSystem().getPath(configuration.getRootDir(), fileName);
	}

}
