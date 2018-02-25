package cz.gattserver.grass3.monitor.web;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.monitor.facade.MonitorFacade;
import cz.gattserver.grass3.monitor.processor.ConsoleOutputTO;
import cz.gattserver.grass3.monitor.web.label.FAILMonitorItem;
import cz.gattserver.grass3.monitor.web.label.OKMonitorItem;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.MailService;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.ui.H2Label;

public class MonitorPage extends OneColumnPage {

	@Autowired
	private MonitorFacade monitorFacade;

	@Autowired
	private MailService mailService;

	private VerticalLayout layout;

	public MonitorPage(GrassRequest request) {
		super(request);
	}

	private VerticalLayout createMonitorPart(String caption) {
		VerticalLayout partLayout = new VerticalLayout();
		layout.addComponent(partLayout);
		partLayout.setMargin(true);
		partLayout.setSpacing(false);
		partLayout.addComponent(new H2Label(caption));
		return partLayout;
	}

	private String humanFormat(long value) {
		return HumanBytesSizeFormatter.format(value, false);
	}

	private void createSystemPart() {
		VerticalLayout jvmLayout = createMonitorPart("System");
		ConsoleOutputTO to = monitorFacade.getUptime();
		if (to.isSuccess()) {
			jvmLayout.addComponent(new OKMonitorItem(monitorFacade.getUptime().getOutput()));
		} else {
			jvmLayout.addComponent(new FAILMonitorItem("System uptime info není dostupné"));
		}

		to = monitorFacade.getMemoryStatus();
		if (to.isSuccess()) {
			String[] values = to.getOutput().split("\n");
			if (values.length != 6) {
				createMemoryFailInfo(jvmLayout);
			} else {
				try {
					// * 1000 protože údaje jsou v KB
					long total = Long.parseLong(values[0]) * 1000;
					long used = Long.parseLong(values[1]) * 1000;
					long free = Long.parseLong(values[2]) * 1000;
					// int shared = Long.parseLong(values[3]);
					// int buffCache = Long.parseLong(values[4]);
					// int available = Long.parseLong(values[5]);

					HorizontalLayout itemLayout = new HorizontalLayout();
					itemLayout.setSpacing(true);
					float usedRation = (float) used / total;
					String usedPerc = NumberFormat.getIntegerInstance().format(usedRation * 100) + "%";
					ProgressBar pb = new ProgressBar();
					pb.setValue(usedRation);
					pb.setWidth("200px");
					itemLayout.addComponent(new OKMonitorItem("obsazeno " + humanFormat(used) + " (" + usedPerc + ") z "
							+ humanFormat(total) + "; volno " + humanFormat(free), pb));
					((AbstractOrderedLayout) pb.getParent()).setComponentAlignment(pb, Alignment.MIDDLE_CENTER);
					jvmLayout.addComponent(itemLayout);
				} catch (NumberFormatException e) {
					createMemoryFailInfo(jvmLayout);
				}
			}
		} else {
			createMemoryFailInfo(jvmLayout);
		}
	}

	private void createMemoryFailInfo(VerticalLayout jvmLayout) {
		jvmLayout.addComponent(new FAILMonitorItem("System memory info není dostupné"));
	}

	private void createJVMPart() {
		VerticalLayout jvmLayout = createMonitorPart("JVM");

		// JVM Uptime
		try {
			long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
			long secondsInMilli = 1000;
			long minutesInMilli = secondsInMilli * 60;
			long hoursInMilli = minutesInMilli * 60;
			long daysInMilli = hoursInMilli * 24;

			long elapsedDays = uptime / daysInMilli;
			uptime = uptime % daysInMilli;

			long elapsedHours = uptime / hoursInMilli;
			uptime = uptime % hoursInMilli;

			long elapsedMinutes = uptime / minutesInMilli;
			uptime = uptime % minutesInMilli;

			long elapsedSeconds = uptime / secondsInMilli;
			jvmLayout.addComponent(
					new OKMonitorItem(String.format("JVM uptime: %d days, %d hours, %d minutes, %d seconds%n",
							elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds)));
		} catch (Exception e) {
			jvmLayout.addComponent(new FAILMonitorItem("JVM uptime info není dostupné"));
		}

		try {
			ThreadMXBean tb = ManagementFactory.getThreadMXBean();
			jvmLayout.addComponent(new OKMonitorItem(
					"Aktuální stav vláken: " + tb.getThreadCount() + " peak: " + tb.getPeakThreadCount()));
		} catch (Exception e) {
			jvmLayout.addComponent(new FAILMonitorItem("JVM thread info není dostupné"));
		}
	}

	private void createMountsPart() {
		VerticalLayout mountsLayout = createMonitorPart("Mount points");
		ConsoleOutputTO mouted = monitorFacade.getDiskMounts();
		if (mouted.isSuccess()) {
			mountsLayout.addComponent(new OKMonitorItem(mouted.getOutput()));
		} else {
			mountsLayout.addComponent(new FAILMonitorItem("Mount points info není dostupné"));
		}
	}

	private void createBackupPart() {
		VerticalLayout backupLayout = createMonitorPart("Backup");
		ConsoleOutputTO mouted = monitorFacade.getBackupDiskMounted();
		if (monitorFacade.getBackupDiskMounted().isSuccess() && Boolean.parseBoolean(mouted.getOutput())) {
			backupLayout.addComponent(new OKMonitorItem(monitorFacade.getLastTimeOfBackup().getOutput()));
		} else {
			backupLayout.addComponent(new FAILMonitorItem("Backup disk není připojen"));
		}
	}

	private void createDisksPart() {
		VerticalLayout diskLayout = createMonitorPart("Disk status");

		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			// win
			for (Path root : FileSystems.getDefault().getRootDirectories()) {
				String name = root.toString();
				try {
					FileStore store = Files.getFileStore(root);
					HorizontalLayout itemLayout = analyzeStore(store, name);
					if (itemLayout != null)
						diskLayout.addComponent(itemLayout);
				} catch (IOException e) {
					diskLayout.addComponent(new FAILMonitorItem(name + " info není dostupné"));
				}
			}
		} else {
			// unix
			for (FileStore store : FileSystems.getDefault().getFileStores()) {
				String name = store.name();
				try {
					HorizontalLayout itemLayout = analyzeStore(store, name);
					if (itemLayout != null)
						diskLayout.addComponent(itemLayout);
				} catch (IOException e) {
					diskLayout.addComponent(new FAILMonitorItem(name + " info není dostupné"));
				}

			}
		}
	}

	private HorizontalLayout analyzeStore(FileStore store, String name) throws IOException {
		HorizontalLayout itemLayout = new HorizontalLayout();
		itemLayout.setSpacing(true);
		long total = store.getTotalSpace();
		// pokud je velikost disku 0, pak jde o virtuální skupinu jako
		// proc, sys apod. -- ty stejně nechci zobrazovat
		if (total == 0)
			return null;
		long usable = store.getUsableSpace();
		long used = total - usable;
		float usedRation = (float) used / total;
		String usedPerc = NumberFormat.getIntegerInstance().format(usedRation * 100) + "%";
		ProgressBar pb = new ProgressBar();
		pb.setValue(usedRation);
		pb.setWidth("200px");
		itemLayout.addComponent(new OKMonitorItem(name + " [" + store.type() + "] obsazeno " + humanFormat(used) + " ("
				+ usedPerc + ") z " + humanFormat(total) + "; volno " + humanFormat(usable), pb));
		((AbstractOrderedLayout) pb.getParent()).setComponentAlignment(pb, Alignment.MIDDLE_CENTER);
		return itemLayout;
	}

	@Override
	protected Component createContent() {
		layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(true);

		// System
		createSystemPart();

		// JVM Uptime
		createJVMPart();

		// Mount points
		createMountsPart();

		// Backup disk
		createBackupPart();

		// Úložiště
		createDisksPart();

		// Mail test
		VerticalLayout mailLayout = createMonitorPart("Mail test");
		Button testMailBtn = new Button("Test mail", e -> {
			mailService.sendToAdmin("Test", "Test mail");
		});
		mailLayout.addComponent(testMailBtn);

		return layout;
	}
}
