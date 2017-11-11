package cz.gattserver.grass3.pg;

import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.config.ConfigurationService;
import cz.gattserver.grass3.pg.config.PhotogalleryConfiguration;
import cz.gattserver.grass3.server.AbstractGrassRequestHandler;

@Component
public class PhotogalleryRequestHandler extends AbstractGrassRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Autowired
	private ConfigurationService configurationService;

	public PhotogalleryRequestHandler() {
		super(PhotogalleryConfiguration.PHOTOGALLERY_PATH);
	}

	@Override
	protected File getFile(String fileName) throws FileNotFoundException {
		PhotogalleryConfiguration configuration = new PhotogalleryConfiguration();
		configurationService.loadConfiguration(configuration);
		return new File(configuration.getRootDir() + "/" + fileName);
	}

}
