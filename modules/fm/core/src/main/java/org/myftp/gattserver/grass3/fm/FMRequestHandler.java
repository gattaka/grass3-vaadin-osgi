package org.myftp.gattserver.grass3.fm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.myftp.gattserver.grass3.config.ConfigurationUtils;
import org.myftp.gattserver.grass3.fm.config.FMConfiguration;
import org.myftp.gattserver.grass3.util.impl.AbstractGrassRequestHandler;
import org.springframework.stereotype.Component;

// TODO

//@Component
public class FMRequestHandler extends AbstractGrassRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	public FMRequestHandler() {
		super(FMConfiguration.FM_PATH);
	}

	/**
	 * Zjistí dle aktuální konfigurace výstupní adresář
	 */
	private String getOutputPath() {
		try {
			return new ConfigurationUtils<FMConfiguration>(
					new FMConfiguration(), FMConfiguration.CONFIG_PATH)
					.loadExistingOrCreateNewConfiguration().getRootDir();
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;	// tohle by se nemělo stát
		}
	}

	@Override
	protected String getMimeType(String fileName) {
		// TODO
		return "image/png png";
	}

	@Override
	protected InputStream getResourceStream(String fileName)
			throws FileNotFoundException {
		File file = new File(getOutputPath() + "/" + fileName);
		return new BufferedInputStream(new FileInputStream(file));
	}

}
