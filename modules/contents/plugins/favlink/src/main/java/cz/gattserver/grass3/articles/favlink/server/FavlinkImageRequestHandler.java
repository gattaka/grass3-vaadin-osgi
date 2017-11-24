package cz.gattserver.grass3.articles.favlink.server;

import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass3.server.AbstractGrassRequestHandler;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.web.common.SpringContextHelper;

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
		ConfigurationService configurationService = (ConfigurationService) SpringContextHelper.getContext()
				.getBean(ConfigurationService.class);

		FavlinkConfiguration configuration = new FavlinkConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration.getOutputPath();
	}

	@Override
	protected File getFile(String fileName) throws FileNotFoundException {
		return new File(getOutputPath() + "/" + fileName);
	}

}
