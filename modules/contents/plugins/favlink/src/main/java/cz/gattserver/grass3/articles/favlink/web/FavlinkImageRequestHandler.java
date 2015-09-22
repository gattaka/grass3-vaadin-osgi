package cz.gattserver.grass3.articles.favlink.web;

import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.articles.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass3.config.IConfigurationService;
import cz.gattserver.grass3.ui.util.impl.AbstractGrassRequestHandler;

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
	protected File getFile(String fileName) throws FileNotFoundException {
		return new File(getOutputPath() + "/" + fileName);
	}

}
