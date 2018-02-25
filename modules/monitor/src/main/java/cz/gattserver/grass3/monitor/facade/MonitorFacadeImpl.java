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
		// #!/bin/bash
		//
		// disk=$( df -h | grep backup )
		//
		// if [ -z "$disk" ]
		// then echo "false"
		// else echo "true"
		// fi
		return runScript("isBackupDiskMounted");
	}

	@Override
	public ConsoleOutputTO getMemoryStatus() {
		// #!/bin/bash
		// free | grep 'Mem' | grep -o [0-9]*
		return runScript("getMemoryStatus");
	}

	@Override
	public ConsoleOutputTO getLastTimeOfBackup() {
		// #!/bin/bash
		// echo -n "SRV "
		// tail -n 1 /mnt/backup/srv-backup.log
		// echo -n "FTP "
		// tail -n 1 /mnt/backup/ftp-backup.log
		return runScript("getLastTimeOfBackup");
	}

	@Override
	public ConsoleOutputTO getDiskMounts() {
		// #!/bin/bash
		// /usr/bin/mount | egrep '^/'
		return runScript("getDiskMounts");
	}

}
