package org.myftp.gattserver.grass3.articles.latex.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.myftp.gattserver.grass3.articles.latex.config.LatexConfiguration;
import org.myftp.gattserver.grass3.articles.parser.exceptions.ParserException;
import org.myftp.gattserver.grass3.config.ConfigurationUtils;
import org.myftp.gattserver.grass3.util.impl.AbstractGrassRequestHandler;
import org.springframework.stereotype.Component;

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
		try {
			return new ConfigurationUtils<LatexConfiguration>(
					new LatexConfiguration(), LatexConfiguration.CONFIG_PATH)
					.loadExistingOrCreateNewConfiguration().getOutputPath();
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new ParserException();
		}
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
