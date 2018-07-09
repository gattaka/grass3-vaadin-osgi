package cz.gattserver.grass3.services.impl;

import java.util.Properties;

import cz.gattserver.grass3.services.VersionInfoService;

public class VersionInfoServiceImpl implements VersionInfoService {

	private Properties versionProperties;

	public void setVersionProperties(Properties appProperties) {
		this.versionProperties = appProperties;
	}

	@Override
	public String getProjectVersion() {
		return versionProperties.getProperty("version");
	}
}
