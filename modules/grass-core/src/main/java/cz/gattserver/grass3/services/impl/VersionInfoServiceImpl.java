package cz.gattserver.grass3.services.impl;

import cz.gattserver.grass3.services.VersionInfoService;

public class VersionInfoServiceImpl implements VersionInfoService {

	private String version;

	public void setVersionProperties(String version) {
		this.version = version;
	}

	@Override
	public String getProjectVersion() {
		return version;
	}
}
