package org.myftp.gattserver.grass3.monitor.facade;

import org.myftp.gattserver.grass3.monitor.config.MonitorConfiguration;

public interface IMonitorFacade {

	public String getUptime();

	public boolean isBackupDiskMounted();

	public String getLastTimeOfBackup();

	public String getBackupDiskSizeInfo();

	MonitorConfiguration getConfiguration();

	void storeConfiguration(MonitorConfiguration configuration);

}
