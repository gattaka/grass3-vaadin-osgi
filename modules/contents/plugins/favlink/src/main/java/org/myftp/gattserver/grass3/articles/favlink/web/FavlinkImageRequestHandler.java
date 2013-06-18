package org.myftp.gattserver.grass3.articles.favlink.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.articles.favlink.config.FavlinkConfiguration;
import org.myftp.gattserver.grass3.config.IConfigurationService;
import org.myftp.gattserver.grass3.util.impl.AbstractGrassRequestHandler;
import org.springframework.stereotype.Component;

@Component
public class FavlinkImageRequestHandler extends AbstractGrassRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	public FavlinkImageRequestHandler() {
		super(FavlinkConfiguration.IMAGE_PATH_ALIAS);
	}

	/**
	 * Zjistí dle aktuální konfigurace výstupní adresář
	 */
	private String getOutputPath() {
		IConfigurationService configurationService = (IConfigurationService) SpringContextHelper
				.getBean("configurationService");

		FavlinkConfiguration configuration = new FavlinkConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration.getOutputPath();
	}

	@Override
	protected String getMimeType(String fileName) {
		return "image/png png";
	}

	@Override
	protected InputStream getResourceStream(String fileName)
			throws FileNotFoundException {
		File file = new File(getOutputPath() + "/" + fileName);
		return new BufferedInputStream(new FileInputStream(file));
	}

}
