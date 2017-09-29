package cz.gattserver.grass3.fm;

import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.config.ConfigurationService;
import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.ui.util.impl.AbstractGrassRequestHandler;

@Component
public class FMRequestHandler extends AbstractGrassRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Autowired
	private ConfigurationService configurationService;

	public FMRequestHandler() {
		super(FMConfiguration.FM_PATH);
	}

	@Override
	protected File getFile(String fileName) throws FileNotFoundException {
		FMConfiguration configuration = new FMConfiguration();
		configurationService.loadConfiguration(configuration);
		return new File(configuration.getRootDir() + "/" + fileName);
	}

}
