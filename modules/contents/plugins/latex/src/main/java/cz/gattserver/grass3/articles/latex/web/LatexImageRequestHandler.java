package cz.gattserver.grass3.articles.latex.web;

import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.articles.latex.config.LatexConfiguration;
import cz.gattserver.grass3.config.ConfigurationService;
import cz.gattserver.grass3.ui.util.impl.AbstractGrassRequestHandler;

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
		ConfigurationService configurationService = (ConfigurationService) SpringContextHelper
				.getBean("configurationService");

		LatexConfiguration configuration = new LatexConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration.getOutputPath();
	}

	@Override
	protected File getFile(String fileName) throws FileNotFoundException {
		return new File(getOutputPath() + "/" + fileName);
	}

}
