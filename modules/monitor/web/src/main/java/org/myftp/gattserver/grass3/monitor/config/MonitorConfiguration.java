package org.myftp.gattserver.grass3.monitor.config;

import org.myftp.gattserver.grass3.config.AbstractConfiguration;

public class MonitorConfiguration extends AbstractConfiguration {

	private String scriptsDir = "grass-monitor-scripts";

	public MonitorConfiguration() {
		super("org.myftp.gattserver.grass3.monitor");
	}

	public String getScriptsDir() {
		return scriptsDir;
	}

	public void setScriptsDir(String scriptsDir) {
		this.scriptsDir = scriptsDir;
	}

}
