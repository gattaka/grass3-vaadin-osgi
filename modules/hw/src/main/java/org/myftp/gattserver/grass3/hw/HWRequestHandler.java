package org.myftp.gattserver.grass3.hw;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.config.IConfigurationService;
import org.myftp.gattserver.grass3.hw.config.HWConfiguration;
import org.myftp.gattserver.grass3.ui.util.AbstractFileRequestHandler;

public class HWRequestHandler extends AbstractFileRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	public HWRequestHandler() {
		super(HWConfiguration.FM_PATH);
	}

	/**
	 * Zjistí dle aktuální konfigurace kořenový adresář
	 */
	@Override
	protected String getRootDir() {
		IConfigurationService configurationService = (IConfigurationService) SpringContextHelper
				.getBean("configurationService");
		HWConfiguration configuration = new HWConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration.getRootDir();
	}

}
