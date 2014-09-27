package cz.gattserver.grass3.fm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.config.IConfigurationService;
import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.ui.util.impl.AbstractGrassRequestHandler;

@Component
public class FMRequestHandler extends AbstractGrassRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Autowired
	private IConfigurationService configurationService;

	public FMRequestHandler() {
		super(FMConfiguration.FM_PATH);
	}

	@Override
	protected InputStream getResourceStream(String fileName) throws FileNotFoundException {
		FMConfiguration configuration = new FMConfiguration();
		configurationService.loadConfiguration(configuration);
		File file = new File(configuration.getRootDir() + "/" + fileName);
		return new BufferedInputStream(new FileInputStream(file));
	}

}
