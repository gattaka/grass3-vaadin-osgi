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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cz.gattserver.grass3.monitor.config.MonitorConfiguration;
import cz.gattserver.grass3.monitor.processor.Console;
import cz.gattserver.grass3.monitor.processor.ConsoleOutputTO;
import cz.gattserver.grass3.monitor.processor.item.BackupDiskMountedMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskStatusMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMMemoryMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMPIDMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMThreadsMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMUptimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.LastBackupTimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.MonitorState;
import cz.gattserver.grass3.monitor.processor.item.SMARTMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.ServerServiceMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemMemoryMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemSwapMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemUptimeMonitorItemTO;
import cz.gattserver.grass3.services.ConfigurationService;

@Transactional
@Component
public class MonitorFacadeImpl implements MonitorFacade {

	private static final int HTTP_TEST_TIMEOUT = 5000;

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
		// #!/bin/bash
		// /usr/bin/mount | egrep '^/'
		ConsoleOutputTO to = runScript("getDiskMounts");
		if (!to.isSuccess())
			return new ArrayList<>();
		String mounts[] = to.getOutput().split("\n");
		Map<String, String> devToMount = new HashMap<>();
		for (String mount : mounts) {
			String info[] = mount.split(" ");
			devToMount.put(info[0], info[2]);
		}

		List<DiskStatusMonitorItemTO> disks = new ArrayList<>();
		for (FileStore store : FileSystems.getDefault().getFileStores()) {
			String mount = devToMount.get(store.name());
			if (mount == null)
				continue;
			DiskStatusMonitorItemTO itemTO = new DiskStatusMonitorItemTO();
			itemTO.setName(store.name());
			itemTO.setMount(mount);
			try {
				if (!analyzeStore(itemTO, store))
					continue;
				itemTO.setMonitorState(MonitorState.SUCCESS);
			} catch (IOException e) {
				itemTO.setMonitorState(MonitorState.UNAVAILABLE);
			}
			disks.add(itemTO);
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

		ServerServiceMonitorItemTO lichTO = new ServerServiceMonitorItemTO("LichEngine", "http://gattserver.cz:1337");
		testResponseCode(lichTO);
		items.add(lichTO);

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

	@Override
	public List<SMARTMonitorItemTO> getSMARTInfo() {
		final String TIME_HEADER = "SYSLOG_TIMESTAMP";
		final String PRIORITY_HEADER = "PRIORITY";
		final String MESSAGE_HEADER = "MESSAGE";

		List<SMARTMonitorItemTO> items = new ArrayList<>();
		// /usr/bin/journalctl -e -u smartd -S 2019-11-03 --lines=10
		// --output-fields=SYSLOG_TIMESTAMP,PRIORITY,MESSAGE -o json
		ConsoleOutputTO out = runScript("getSmartStatus");
		if (out.isSuccess()) {
			try {
				String[] lines = out.getOutput().split("\n");
				for (String line : lines) {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode jsonNode = mapper.readTree(line);
					String message = jsonNode.get(MESSAGE_HEADER).asText();
					int priority = jsonNode.get(PRIORITY_HEADER).asInt();
					String time = jsonNode.get(TIME_HEADER).asText();
					SMARTMonitorItemTO to = new SMARTMonitorItemTO(time, message);

					// https://www.freedesktop.org/software/systemd/man/journalctl.html
					switch (priority) {
					case 0: // "emerg" (0)
					case 1: // "alert" (1)
					case 2: // "crit" (2)
					case 3: // "err" (3)
					case 4: // "warning" (4)
						to.setMonitorState(MonitorState.ERROR);
						items.add(to);
						break;
					case 5: // "notice" (5)
					case 6: // "info" (6)
					case 7: // "debug" (7)
					default:
						to.setMonitorState(MonitorState.SUCCESS);
						break;
					}
				}
				if (items.isEmpty()) {
					SMARTMonitorItemTO to = new SMARTMonitorItemTO("-", "Vše OK");
					to.setMonitorState(MonitorState.SUCCESS);
					items.add(to);
				}
			} catch (Exception e) {
				return createErrorOutput("Nezdařilo se zpracovat JSON výstup smartd");
			}
		} else {
			return createErrorOutput("Nezdařilo se získat přehled smartd");
		}
		return items;
	}

	private List<SMARTMonitorItemTO> createErrorOutput(String reason) {
		SMARTMonitorItemTO item = new SMARTMonitorItemTO();
		item.setStateDetails(reason);
		item.setMonitorState(MonitorState.UNAVAILABLE);
		return Arrays.asList(item);
	}

}
