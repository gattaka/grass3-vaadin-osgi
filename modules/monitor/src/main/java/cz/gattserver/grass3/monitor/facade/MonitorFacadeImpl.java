package cz.gattserver.grass3.monitor.facade;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.monitor.config.MonitorConfiguration;
import cz.gattserver.grass3.monitor.processor.Console;
import cz.gattserver.grass3.monitor.processor.ConsoleOutputTO;
import cz.gattserver.grass3.monitor.processor.item.BackupDiskMountedMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskMountsMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskStatusMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMHeapMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMMemoryMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMPIDMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMThreadsMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMUptimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.LastBackupTimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.MonitorState;
import cz.gattserver.grass3.monitor.processor.item.ServerServiceMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemMemoryMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemSwapMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemUptimeMonitorItemTO;
import cz.gattserver.grass3.services.ConfigurationService;

@Transactional
@Component
public class MonitorFacadeImpl implements MonitorFacade {

	private static final int HTTP_TEST_TIMEOUT = 5000;
	private static Logger logger = LoggerFactory.getLogger(MonitorFacadeImpl.class);

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

	private ConsoleOutputTO runScript(String script, String... param) {
		String scriptsDir = getConfiguration().getScriptsDir();
		List<String> items = new ArrayList<>();
		items.add(scriptsDir + "/" + script + ".sh");
		for (String p : param)
			items.add(p);
		return Console.executeCommand(items);
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
	public SystemSwapMonitorItemTO getSystemSwapStatus() {
		// #!/bin/bash
		// free | grep 'Swap' | grep -o [0-9]*
		ConsoleOutputTO to = runScript("getSwapStatus");
		SystemSwapMonitorItemTO itemTO = new SystemSwapMonitorItemTO();

		String[] values = to.getOutput().split("\n");
		if (values.length != 3) {
			itemTO.setMonitorState(MonitorState.UNAVAILABLE);
			return itemTO;
		} else {
			try {
				// * 1000 protože údaje jsou v KB
				itemTO.setTotal(Long.parseLong(values[0]) * 1000);
				itemTO.setUsed(Long.parseLong(values[1]) * 1000);
				itemTO.setFree(Long.parseLong(values[2]) * 1000);
			} catch (NumberFormatException e) {
				itemTO.setMonitorState(MonitorState.UNAVAILABLE);
				return itemTO;
			}
		}

		itemTO.setMonitorState(MonitorState.SUCCESS);
		return itemTO;
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
	public List<LastBackupTimeMonitorItemTO> getLastTimeOfBackup() {
		// #!/bin/bash
		// echo -n "SRV "
		// tail -n 1 /mnt/backup/srv-backup.log
		// echo -n "SYS "
		// tail -n 1 /mnt/backup/srv-systemctl-backup.log
		// echo -n "FTP "
		// tail -n 1 /mnt/backup/ftp-backup.log
		ConsoleOutputTO to = runScript("getLastTimeOfBackup");
		List<LastBackupTimeMonitorItemTO> list = new ArrayList<>();

		String dummTarget = "TTT Last backup:  ";
		String dummyDate = "HH:MM:SS DD.MM.YYYY";
		String dummyLog = dummTarget + dummyDate;

		if (to.isSuccess()) {
			for (String part : to.getOutput().split("\n")) {
				LastBackupTimeMonitorItemTO itemTO = new LastBackupTimeMonitorItemTO();
				if (part.length() == dummyLog.length()) {
					String target = part.substring(0, 3);
					String date = part.substring(dummTarget.length());
					LocalDateTime lastBackup = LocalDateTime.parse(date,
							DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
					itemTO.setValue(target + ": poslední záloha byla provedena " + date);
					// poslední záloha nesmí být starší než 24h
					if (lastBackup.isBefore(LocalDateTime.now().minusHours(24)))
						itemTO.setMonitorState(MonitorState.ERROR);
					else
						itemTO.setMonitorState(MonitorState.SUCCESS);
					itemTO.setLastTime(lastBackup);
				} else {
					// nejsou podklady pro info o poslední záloze? Chyba!
					itemTO.setMonitorState(MonitorState.ERROR);
				}
				list.add(itemTO);
			}
		} else {
			LastBackupTimeMonitorItemTO itemTO = new LastBackupTimeMonitorItemTO();
			itemTO.setMonitorState(MonitorState.UNAVAILABLE);
			list.add(itemTO);
		}
		return list;
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
	public JVMMemoryMonitorItemTO getJVMMemory() {
		JVMMemoryMonitorItemTO to = new JVMMemoryMonitorItemTO();
		try {
			Runtime runtime = Runtime.getRuntime();
			to.setUsedMemory(runtime.totalMemory() - runtime.freeMemory());
			to.setFreeMemory(runtime.freeMemory());
			to.setTotalMemory(runtime.totalMemory());
			to.setMaxMemory(runtime.maxMemory());
			to.setMonitorState(MonitorState.SUCCESS);
		} catch (Exception e) {
			to.setMonitorState(MonitorState.UNAVAILABLE);
		}
		return to;
	}

	@Override
	public JVMPIDMonitorItemTO getJVMPID() {
		JVMPIDMonitorItemTO to = new JVMPIDMonitorItemTO();
		try {
			// https://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
			to.setPid(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
			to.setMonitorState(MonitorState.SUCCESS);
		} catch (Exception e) {
			to.setMonitorState(MonitorState.UNAVAILABLE);
		}
		return to;
	}

	@Override
	public JVMHeapMonitorItemTO getJVMHeap() {
		JVMHeapMonitorItemTO to = new JVMHeapMonitorItemTO();
		try {
			Path tempDirWithPrefix = Files.createTempDirectory("grassMonitorDump");

			Path outFile = tempDirWithPrefix.resolve("out");
			String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

			runScript("getJmapList", pid, outFile.toAbsolutePath().toString());

			Thread.sleep(2000);

			// outFile =
			// java.nio.file.Paths.get("c:/Users/gatta/Downloads").resolve("out");
			Pattern pattern = Pattern.compile("[0-9]+:\\s+[0-9]+\\s+[0-9]+.+");
			List<String> lines = Files.readAllLines(outFile);
			for (String s : lines) {
				s = s.trim();
				if (!pattern.matcher(s).matches())
					continue;
				String[] parts = s.split("\\s+");
				to.getLines()
						.add(new JVMHeapMonitorItemTO.Line(
								Integer.parseInt(parts[0].substring(0, parts[0].length() - 1)),
								Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), parts[3]));
			}

			to.setFileName(outFile.toAbsolutePath().toString());

			to.setMonitorState(MonitorState.SUCCESS);
		} catch (Exception e) {
			to.setMonitorState(MonitorState.UNAVAILABLE);
			logger.error("Dump se nepovedl", e);
		}
		return to;
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

	@Override
	public List<ServerServiceMonitorItemTO> getServerServicesStatus() {
		List<ServerServiceMonitorItemTO> items = new ArrayList<>();

		ServerServiceMonitorItemTO syncthingTO = new ServerServiceMonitorItemTO("Syncthing",
				"http://gattserver.cz:8127");
		testResponseCode(syncthingTO, true);
		items.add(syncthingTO);

		ServerServiceMonitorItemTO nexusTO = new ServerServiceMonitorItemTO("Sonatype Nexus",
				"http://gattserver.cz:8081");
		testResponseCode(nexusTO);
		items.add(nexusTO);

		ServerServiceMonitorItemTO nexusSecureTO = new ServerServiceMonitorItemTO("Sonatype Nexus HTTPS",
				"https://www.gattserver.cz:8843");
		testResponseCode(nexusSecureTO);
		items.add(nexusSecureTO);

		ServerServiceMonitorItemTO sonarTO = new ServerServiceMonitorItemTO("SonarQube", "http://gattserver.cz:9000");
		testResponseCode(sonarTO);
		items.add(sonarTO);

		// ServerServiceMonitorItemTO h2TO = new
		// ServerServiceMonitorItemTO("H2", "http://gattserver.cz:8082");
		// testResponseCode(h2TO);
		// items.add(h2TO);

		ServerServiceMonitorItemTO lichTO = new ServerServiceMonitorItemTO("LichEngine", "http://gattserver.cz:1337");
		testResponseCode(lichTO);
		items.add(lichTO);

		ServerServiceMonitorItemTO catacombsTO = new ServerServiceMonitorItemTO("Catacombs",
				"http://gattserver.cz:8333");
		testResponseCode(catacombsTO);
		items.add(catacombsTO);

		return items;
	}

	private void testResponseCode(ServerServiceMonitorItemTO itemTO) {
		testResponseCode(itemTO, false);
	}

	private void testResponseCode(ServerServiceMonitorItemTO itemTO, boolean anyCode) {
		try {
			URL url = new URL(itemTO.getAddress());
			URLConnection uc = url.openConnection();
			if (uc != null && uc instanceof HttpURLConnection) {
				// HttpURLConnection
				HttpURLConnection hc = (HttpURLConnection) uc;
				hc.setInstanceFollowRedirects(true);

				// bez agenta to často hodí 403 Forbidden, protože si myslí,
				// že jsem asi bot ... (což vlastně jsem)
				hc.setRequestProperty("User-Agent", "Mozilla");
				hc.setConnectTimeout(HTTP_TEST_TIMEOUT);
				hc.setReadTimeout(HTTP_TEST_TIMEOUT);
				hc.connect();
				itemTO.setResponseCode(hc.getResponseCode());
				if (anyCode || itemTO.getResponseCode() >= 200 && itemTO.getResponseCode() < 300)
					itemTO.setMonitorState(MonitorState.SUCCESS);
				else
					itemTO.setMonitorState(MonitorState.ERROR);
			}
		} catch (SocketTimeoutException e) {
			itemTO.setMonitorState(MonitorState.ERROR);
		} catch (Exception e) {
			itemTO.setMonitorState(MonitorState.UNAVAILABLE);
		}
	}

}
