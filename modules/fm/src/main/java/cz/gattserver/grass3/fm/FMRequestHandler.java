package cz.gattserver.grass3.fm;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.server.AbstractConfiguratedPathRequestHandler;

@Component("fmRequestHandler")
public class FMRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	public FMRequestHandler() {
		super(FMConfiguration.FM_PATH);
	}

	@Override
	protected Path getPath(String fileName) throws FileNotFoundException {
		FMConfiguration configuration = new FMConfiguration();
		getConfigurationService().loadConfiguration(configuration);
		return getFileSystemService().getFileSystem().getPath(configuration.getRootDir(), fileName);
	}

}
