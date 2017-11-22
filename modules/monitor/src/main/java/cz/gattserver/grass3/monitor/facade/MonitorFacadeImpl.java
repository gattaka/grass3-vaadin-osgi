package cz.gattserver.grass3.monitor.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.monitor.config.MonitorConfiguration;
import cz.gattserver.grass3.monitor.processor.Console;
import cz.gattserver.grass3.monitor.processor.ConsoleOutputTO;
import cz.gattserver.grass3.services.ConfigurationService;

@Transactional
@Component
public class MonitorFacadeImpl implements MonitorFacade {

	@Autowired
	private ConfigurationService configurationService;

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

	private ConsoleOutputTO runScript(String script) {
		String scriptsDir = getConfiguration().getScriptsDir();
		return Console.executeCommand(scriptsDir + "/" + script + ".sh");
	}

	@Override
	public ConsoleOutputTO getUptime() {
		return Console.executeCommand("uptime");
	}

	@Override
	public ConsoleOutputTO getBackupDiskMounted() {
		return runScript("isBackupDiskMounted");
	}

	@Override
	public ConsoleOutputTO getLastTimeOfBackup() {
		return runScript("getLastTimeOfBackup");
	}

	@Override
	public ConsoleOutputTO getBackupDiskSizeInfo() {
		return runScript("getBackupDiskSizeInfo");
	}

}
