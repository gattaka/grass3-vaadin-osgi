package cz.gattserver.grass3.fm;

import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.server.AbstractGrassRequestHandler;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.web.common.spring.SpringContextHelper;

@Component
public class FMRequestHandler extends AbstractGrassRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	private transient ConfigurationService configurationService;

	public FMRequestHandler() {
		super(FMConfiguration.FM_PATH);
	}

	private ConfigurationService getConfigurationService() {
		// získává se takhle aby nemusela být transient/serializable
		if (configurationService == null)
			configurationService = SpringContextHelper.getBean(ConfigurationService.class);
		return configurationService;
	}

	@Override
	protected File getFile(String fileName) throws FileNotFoundException {
		FMConfiguration configuration = new FMConfiguration();
		getConfigurationService().loadConfiguration(configuration);
		return new File(configuration.getRootDir(), fileName);
	}

}
