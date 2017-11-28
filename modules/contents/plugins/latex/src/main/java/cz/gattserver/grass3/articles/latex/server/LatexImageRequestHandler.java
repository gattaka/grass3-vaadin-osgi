package cz.gattserver.grass3.articles.latex.server;

import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.latex.config.LatexConfiguration;
import cz.gattserver.grass3.server.AbstractGrassRequestHandler;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.web.common.spring.SpringContextHelper;

@Component
public class LatexImageRequestHandler extends AbstractGrassRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	public LatexImageRequestHandler() {
		super(LatexConfiguration.IMAGE_PATH_ALIAS);
	}

	/**
	 * Zjistí dle aktuální konfigurace výstupní adresář
	 */
	private String getOutputPath() {
		ConfigurationService configurationService = (ConfigurationService) SpringContextHelper.getContext()
				.getBean(ConfigurationService.class);

		LatexConfiguration configuration = new LatexConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration.getOutputPath();
	}

	@Override
	protected File getFile(String fileName) throws FileNotFoundException {
		return new File(getOutputPath() + "/" + fileName);
	}

}