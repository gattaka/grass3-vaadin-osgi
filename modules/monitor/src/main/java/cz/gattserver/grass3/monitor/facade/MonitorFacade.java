package cz.gattserver.grass3.monitor.facade;

import cz.gattserver.grass3.monitor.config.MonitorConfiguration;
import cz.gattserver.grass3.monitor.processor.ConsoleOutputTO;

public interface MonitorFacade {

	public ConsoleOutputTO getUptime();

	public ConsoleOutputTO getBackupDiskMounted();

	public ConsoleOutputTO getLastTimeOfBackup();

	public ConsoleOutputTO getBackupDiskSizeInfo();

	MonitorConfiguration getConfiguration();

	void storeConfiguration(MonitorConfiguration configuration);

}
