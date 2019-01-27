package cz.gattserver.grass3.monitor.facade;

import java.util.List;

import cz.gattserver.grass3.monitor.config.MonitorConfiguration;
import cz.gattserver.grass3.monitor.processor.item.BackupDiskMountedMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskMountsMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskStatusMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMHeapDumpMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMPIDMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMThreadsMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMUptimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.LastBackupTimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.ServerServiceMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemMemoryMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemSwapMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemUptimeMonitorItemTO;

public interface MonitorFacade {

	MonitorConfiguration getConfiguration();

	void storeConfiguration(MonitorConfiguration configuration);

	SystemUptimeMonitorItemTO getSystemUptime();

	SystemMemoryMonitorItemTO getSystemMemoryStatus();

	SystemSwapMonitorItemTO getSystemSwapStatus();

	DiskMountsMonitorItemTO getDiskMounts();

	BackupDiskMountedMonitorItemTO getBackupDiskMounted();

	List<LastBackupTimeMonitorItemTO> getLastTimeOfBackup();

	List<DiskStatusMonitorItemTO> getDiskStatus();

	List<ServerServiceMonitorItemTO> getServerServicesStatus();

	JVMUptimeMonitorItemTO getJVMUptime();

	JVMThreadsMonitorItemTO getJVMThreads();

	JVMPIDMonitorItemTO getJVMPID();

	JVMHeapDumpMonitorItemTO getJVMHeapDump();

}
