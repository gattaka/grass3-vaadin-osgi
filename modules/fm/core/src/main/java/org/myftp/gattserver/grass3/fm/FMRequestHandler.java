package org.myftp.gattserver.grass3.fm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.config.IConfigurationService;
import org.myftp.gattserver.grass3.fm.config.FMConfiguration;
import org.myftp.gattserver.grass3.util.impl.AbstractGrassRequestHandler;
import org.springframework.stereotype.Component;

// TODO připraveno na stahování/otevírání souborů

//@Component
public class FMRequestHandler extends AbstractGrassRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	public FMRequestHandler() {
		super(FMConfiguration.FM_PATH);
	}

	/**
	 * Zjistí dle aktuální konfigurace kořenový adresář
	 */
	private String getRootDir() {
		IConfigurationService configurationService = (IConfigurationService) SpringContextHelper
				.getBean("configurationService");
		FMConfiguration configuration = new FMConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration.getRootDir();
	}

	@Override
	protected String getMimeType(String fileName) {
		// TODO
		return "image/png png";
	}

	@Override
	protected InputStream getResourceStream(String fileName)
			throws FileNotFoundException {
		File file = new File(getRootDir() + "/" + fileName);
		return new BufferedInputStream(new FileInputStream(file));
	}

}
