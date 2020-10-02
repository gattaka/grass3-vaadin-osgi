package cz.gattserver.grass3.monitor.facade;

import cz.gattserver.grass3.monitor.config.MonitorConfiguration;
import cz.gattserver.grass3.monitor.processor.item.DiskStatusPartItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMMemoryMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMPIDMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMThreadsMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMUptimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.BackupStatusPartItemTO;
import cz.gattserver.grass3.monitor.processor.item.SMARTPartItemTO;
import cz.gattserver.grass3.monitor.processor.item.ServerServicePartItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemMemoryMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemSwapMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemUptimeMonitorItemTO;

public interface MonitorFacade {

	MonitorConfiguration getConfiguration();

	void storeConfiguration(MonitorConfiguration configuration);

	SystemUptimeMonitorItemTO getSystemUptime();

	SystemMemoryMonitorItemTO getSystemMemoryStatus();

	SystemSwapMonitorItemTO getSystemSwapStatus();

	BackupStatusPartItemTO getBackupStatus();

	DiskStatusPartItemTO getDiskStatus();

	ServerServicePartItemTO getServerServicesStatus();

	JVMUptimeMonitorItemTO getJVMUptime();

	JVMThreadsMonitorItemTO getJVMThreads();

	JVMMemoryMonitorItemTO getJVMMemory();

	JVMPIDMonitorItemTO getJVMPID();

	SMARTPartItemTO getSMARTInfo();

}
