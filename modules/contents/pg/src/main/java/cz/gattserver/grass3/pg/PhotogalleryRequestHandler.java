package cz.gattserver.grass3.pg;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.config.IConfigurationService;
import cz.gattserver.grass3.pg.config.PhotogalleryConfiguration;
import cz.gattserver.grass3.ui.util.impl.AbstractGrassRequestHandler;

@Component
public class PhotogalleryRequestHandler extends AbstractGrassRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Autowired
	private IConfigurationService configurationService;

	public PhotogalleryRequestHandler() {
		super(PhotogalleryConfiguration.PHOTOGALLERY_PATH);
	}

	@Override
	protected InputStream getResourceStream(String fileName) throws FileNotFoundException {
		PhotogalleryConfiguration configuration = new PhotogalleryConfiguration();
		configurationService.loadConfiguration(configuration);
		File file = new File(configuration.getRootDir() + "/" + fileName);
		return new BufferedInputStream(new FileInputStream(file));
	}

}
