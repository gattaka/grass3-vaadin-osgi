package org.myftp.gattserver.grass3.monitor.facade;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.config.IConfigurationService;
import org.myftp.gattserver.grass3.monitor.config.MonitorConfiguration;
import org.myftp.gattserver.grass3.monitor.processor.Console;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("monitorFacade")
public class MonitorFacade implements IMonitorFacade {

	@Resource
	private IConfigurationService configurationService;

	@Override
	public MonitorConfiguration getConfiguration() {
		MonitorConfiguration configuration = new MonitorConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	@Override
	public void storeConfiguration(MonitorConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

	private String runScript(String script) {
		String scriptsDir = getConfiguration().getScriptsDir();
		return Console.executeCommand(scriptsDir + "/" + script + ".sh");
	}

	@Override
	public String getUptime() {
		return Console.executeCommand("uptime");
	}

	@Override
	public boolean isBackupDiskMounted() {
		return Boolean.valueOf(runScript("isBackupDiskMounted"));
	}

	@Override
	public String getLastTimeOfBackup() {
		return runScript("getLastTimeOfBackup");
	}

	@Override
	public String getBackupDiskSizeInfo() {
		return runScript("getBackupDiskSizeInfo");
	}

}
