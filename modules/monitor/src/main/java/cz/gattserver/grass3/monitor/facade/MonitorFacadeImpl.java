package cz.gattserver.grass3.monitor.facade;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.monitor.config.MonitorConfiguration;
import cz.gattserver.grass3.monitor.processor.Console;
import cz.gattserver.grass3.monitor.processor.ConsoleOutputTO;
import cz.gattserver.grass3.monitor.processor.item.BackupDiskMountedMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskMountsMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskStatusMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMThreadsMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMUptimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.LastBackupTimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.MonitorState;
import cz.gattserver.grass3.monitor.processor.item.SystemMemoryMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemUptimeMonitorItemTO;
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
	public SystemUptimeMonitorItemTO getSystemUptime() {
		ConsoleOutputTO to = Console.executeCommand("uptime");
		SystemUptimeMonitorItemTO uptimeTO = new SystemUptimeMonitorItemTO();
		uptimeTO.setMonitorState(to.isSuccess() ? MonitorState.SUCCESS : MonitorState.UNAVAILABLE);
		uptimeTO.setValue(to.getOutput());
		return uptimeTO;
	}

	@Override
	public SystemMemoryMonitorItemTO getSystemMemoryStatus() {
		// #!/bin/bash
		// free | grep 'Mem' | grep -o [0-9]*
		ConsoleOutputTO to = runScript("getMemoryStatus");
		SystemMemoryMonitorItemTO itemTO = new SystemMemoryMonitorItemTO();

		String[] values = to.getOutput().split("\n");
		if (values.length != 6) {
			itemTO.setMonitorState(MonitorState.UNAVAILABLE);
			return itemTO;
		} else {
			try {
				// * 1000 protože údaje jsou v KB
				itemTO.setTotal(Long.parseLong(values[0]) * 1000);
				itemTO.setUsed(Long.parseLong(values[1]) * 1000);
				itemTO.setFree(Long.parseLong(values[2]) * 1000);
				itemTO.setShared(Long.parseLong(values[3]) * 1000);
				itemTO.setBuffCache(Long.parseLong(values[4]) * 1000);
				itemTO.setAvailable(Long.parseLong(values[5]) * 1000);
			} catch (NumberFormatException e) {
				itemTO.setMonitorState(MonitorState.UNAVAILABLE);
				return itemTO;
			}
		}

		itemTO.setMonitorState(MonitorState.SUCCESS);
		return itemTO;
	}

	@Override
	public JVMUptimeMonitorItemTO getJVMUptime() {
		JVMUptimeMonitorItemTO to = new JVMUptimeMonitorItemTO();
		try {
			to.setUptime(ManagementFactory.getRuntimeMXBean().getUptime());
			to.setMonitorState(MonitorState.SUCCESS);
		} catch (Exception e) {
			to.setMonitorState(MonitorState.UNAVAILABLE);
		}
		return to;
	}

	@Override
	public JVMThreadsMonitorItemTO getJVMThreads() {
		JVMThreadsMonitorItemTO to = new JVMThreadsMonitorItemTO();
		try {
			ThreadMXBean tb = ManagementFactory.getThreadMXBean();
			to.setCount(tb.getThreadCount());
			to.setPeak(tb.getPeakThreadCount());
			to.setMonitorState(MonitorState.SUCCESS);
		} catch (Exception e) {
			to.setMonitorState(MonitorState.UNAVAILABLE);
		}
		return to;
	}

	@Override
	public DiskMountsMonitorItemTO getDiskMounts() {
		// #!/bin/bash
		// /usr/bin/mount | egrep '^/'
		ConsoleOutputTO to = runScript("getDiskMounts");
		DiskMountsMonitorItemTO itemTO = new DiskMountsMonitorItemTO();
		itemTO.setMonitorState(to.isSuccess() ? MonitorState.SUCCESS : MonitorState.UNAVAILABLE);
		itemTO.setValue(to.getOutput());
		return itemTO;
	}

	@Override
	public BackupDiskMountedMonitorItemTO getBackupDiskMounted() {
		// #!/bin/bash
		//
		// disk=$( df -h | grep backup )
		//
		// if [ -z "$disk" ]
		// then echo "false"
		// else echo "true"
		// fi
		ConsoleOutputTO to = runScript("isBackupDiskMounted");
		BackupDiskMountedMonitorItemTO itemTO = new BackupDiskMountedMonitorItemTO();
		itemTO.setValue(to.getOutput());
		if (to.isSuccess()) {
			itemTO.setMonitorState(Boolean.parseBoolean(to.getOutput()) ? MonitorState.SUCCESS : MonitorState.ERROR);
		} else {
			itemTO.setMonitorState(MonitorState.UNAVAILABLE);
		}
		return itemTO;
	}

	@Override
	public LastBackupTimeMonitorItemTO getLastTimeOfBackup() {
		// #!/bin/bash
		// echo -n "SRV "
		// tail -n 1 /mnt/backup/srv-backup.log
		// echo -n "FTP "
		// tail -n 1 /mnt/backup/ftp-backup.log
		ConsoleOutputTO to = runScript("getLastTimeOfBackup");
		LastBackupTimeMonitorItemTO itemTO = new LastBackupTimeMonitorItemTO();
		itemTO.setValue(to.getOutput());
		if (to.isSuccess()) {
			// String test = "SRV Last backup: Po úno 26 00:00:23 CET 2018\nFTP
			// Last backup: Po úno 26 00:01:38 CET 2018";
			// TODO parsing času a porovnání, zda nejde o 24+ hodin starou
			// zálohu
			itemTO.setMonitorState(MonitorState.SUCCESS);
		} else {
			itemTO.setMonitorState(MonitorState.UNAVAILABLE);
		}
		return itemTO;
	}

	@Override
	public List<DiskStatusMonitorItemTO> getDiskStatus() {
		List<DiskStatusMonitorItemTO> disks = new ArrayList<>();
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			// win
			for (Path root : FileSystems.getDefault().getRootDirectories()) {
				DiskStatusMonitorItemTO to = new DiskStatusMonitorItemTO();
				to.setName(root.toString());
				try {
					FileStore store = Files.getFileStore(root);
					if (!analyzeStore(to, store))
						continue;
					to.setMonitorState(MonitorState.SUCCESS);
				} catch (IOException e) {
					to.setMonitorState(MonitorState.UNAVAILABLE);
				}
				disks.add(to);
			}
		} else {
			// unix
			for (FileStore store : FileSystems.getDefault().getFileStores()) {
				DiskStatusMonitorItemTO to = new DiskStatusMonitorItemTO();
				to.setName(store.name());
				try {
					if (!analyzeStore(to, store))
						continue;
					to.setMonitorState(MonitorState.SUCCESS);
				} catch (IOException e) {
					to.setMonitorState(MonitorState.UNAVAILABLE);
				}
				disks.add(to);
			}
		}
		return disks;
	}

	private boolean analyzeStore(DiskStatusMonitorItemTO to, FileStore store) throws IOException {
		to.setTotal(store.getTotalSpace());
		// pokud je velikost disku 0, pak jde o virtuální skupinu jako
		// proc, sys apod. -- ty stejně nechci zobrazovat
		if (to.getTotal() == 0)
			return false;
		to.setType(store.type());
		to.setUsable(store.getUsableSpace());
		return true;
	}

}
