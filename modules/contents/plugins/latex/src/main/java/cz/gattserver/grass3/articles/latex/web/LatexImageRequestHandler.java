package cz.gattserver.grass3.articles.latex.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.articles.latex.config.LatexConfiguration;
import cz.gattserver.grass3.config.IConfigurationService;
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
		IConfigurationService configurationService = (IConfigurationService) SpringContextHelper
				.getBean("configurationService");

		LatexConfiguration configuration = new LatexConfiguration();
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
