package cz.gattserver.grass3.monitor.web;

import java.text.NumberFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.monitor.facade.MonitorFacade;
import cz.gattserver.grass3.monitor.processor.item.BackupDiskMountedMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskMountsMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskStatusMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMThreadsMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMUptimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.LastBackupTimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.MonitorState;
import cz.gattserver.grass3.monitor.processor.item.SystemMemoryMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemUptimeMonitorItemTO;
import cz.gattserver.grass3.monitor.web.label.ErrorMonitorDisplay;
import cz.gattserver.grass3.monitor.web.label.WarningMonitorDisplay;
import cz.gattserver.grass3.monitor.web.label.SuccessMonitorDisplay;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.MailService;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.ui.H2Label;
import elemental.json.JsonArray;

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

		/*
		 * Uptime
		 */
		SystemUptimeMonitorItemTO uptimeTO = monitorFacade.getSystemUptime();
		switch (uptimeTO.getMonitorState()) {
		case SUCCESS:
			jvmLayout.addComponent(new SuccessMonitorDisplay(uptimeTO.getValue()));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmLayout.addComponent(new WarningMonitorDisplay("System uptime info není dostupné"));
		}

		/*
		 * Memory
		 */
		SystemMemoryMonitorItemTO memoryTO = monitorFacade.getSystemMemoryStatus();
		switch (memoryTO.getMonitorState()) {
		case SUCCESS:
			HorizontalLayout itemLayout = new HorizontalLayout();
			itemLayout.setSpacing(true);
			String usedPerc = NumberFormat.getIntegerInstance().format(memoryTO.getUsedRation() * 100) + "%";
			ProgressBar pb = new ProgressBar();
			pb.setValue(memoryTO.getUsedRation());
			pb.setWidth("200px");
			itemLayout
					.addComponent(new SuccessMonitorDisplay(
							"obsazeno " + humanFormat(memoryTO.getUsed()) + " (" + usedPerc + ") z "
									+ humanFormat(memoryTO.getTotal()) + "; volno " + humanFormat(memoryTO.getFree()),
							pb));
			((AbstractOrderedLayout) pb.getParent()).setComponentAlignment(pb, Alignment.MIDDLE_CENTER);
			jvmLayout.addComponent(itemLayout);
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmLayout.addComponent(new WarningMonitorDisplay("System memory info není dostupné"));
		}
	}

	private void createJVMPart() {
		VerticalLayout jvmLayout = createMonitorPart("JVM");

		/*
		 * JVM Uptime
		 */
		JVMUptimeMonitorItemTO uptimeTO = monitorFacade.getJVMUptime();
		switch (uptimeTO.getMonitorState()) {
		case SUCCESS:
			jvmLayout.addComponent(new SuccessMonitorDisplay(
					String.format("JVM uptime: %d days, %d hours, %d minutes, %d seconds%n", uptimeTO.getElapsedDays(),
							uptimeTO.getElapsedHours(), uptimeTO.getElapsedMinutes(), uptimeTO.getElapsedSeconds())));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmLayout.addComponent(new WarningMonitorDisplay("JVM uptime info není dostupné"));
		}

		/*
		 * JVM Threads
		 */
		JVMThreadsMonitorItemTO threadsTO = monitorFacade.getJVMThreads();
		switch (threadsTO.getMonitorState()) {
		case SUCCESS:
			jvmLayout.addComponent(new SuccessMonitorDisplay(
					"Aktuální stav vláken: " + threadsTO.getCount() + " peak: " + threadsTO.getPeak()));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmLayout.addComponent(new WarningMonitorDisplay("JVM thread info není dostupné"));
		}
	}

	private void createMountsPart() {
		VerticalLayout mountsLayout = createMonitorPart("Mount points");
		DiskMountsMonitorItemTO to = monitorFacade.getDiskMounts();
		switch (to.getMonitorState()) {
		case SUCCESS:
			mountsLayout.addComponent(new SuccessMonitorDisplay(to.getValue()));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			mountsLayout.addComponent(new WarningMonitorDisplay("Mount points info není dostupné"));
		}
	}

	private void createBackupPart() {
		VerticalLayout backupLayout = createMonitorPart("Backup");
		BackupDiskMountedMonitorItemTO mouted = monitorFacade.getBackupDiskMounted();
		switch (mouted.getMonitorState()) {
		case SUCCESS:
			backupLayout.addComponent(new SuccessMonitorDisplay("Backup disk je připojen"));
			break;
		case ERROR:
			backupLayout.addComponent(new ErrorMonitorDisplay("Backup disk není připojen"));
			break;
		case UNAVAILABLE:
		default:
			backupLayout.addComponent(new WarningMonitorDisplay("Backup disk info není dostupné"));
		}

		if (MonitorState.SUCCESS.equals(mouted.getMonitorState())) {
			List<LastBackupTimeMonitorItemTO> lastBackupTOs = monitorFacade.getLastTimeOfBackup();
			for (LastBackupTimeMonitorItemTO lastBackupTO : lastBackupTOs) {
				switch (lastBackupTO.getMonitorState()) {
				case SUCCESS:
					backupLayout.addComponent(new SuccessMonitorDisplay(lastBackupTO.getValue()));
					break;
				case ERROR:
					backupLayout.addComponent(new ErrorMonitorDisplay(
							lastBackupTO.getValue() + ": Nebyla provedena pravidelná záloha nebo je starší, než 24h"));
					break;
				case UNAVAILABLE:
				default:
					backupLayout.addComponent(
							new WarningMonitorDisplay("Backup disk info o provedení poslední zálohy není dostupné"));
				}
			}
		}

	}

	private void createDisksPart() {
		VerticalLayout diskLayout = createMonitorPart("Disk status");
		List<DiskStatusMonitorItemTO> disks = monitorFacade.getDiskStatus();
		for (DiskStatusMonitorItemTO disk : disks) {
			switch (disk.getMonitorState()) {
			case SUCCESS:
				HorizontalLayout itemLayout = new HorizontalLayout();
				itemLayout.setSpacing(true);
				String usedPerc = NumberFormat.getIntegerInstance().format(disk.getUsedRation() * 100) + "%";
				ProgressBar pb = new ProgressBar();
				pb.setValue(disk.getUsedRation());
				pb.setWidth("200px");
				itemLayout.addComponent(new SuccessMonitorDisplay(disk.getName() + " [" + disk.getType() + "] obsazeno "
						+ humanFormat(disk.getUsed()) + " (" + usedPerc + ") z " + humanFormat(disk.getTotal())
						+ "; volno " + humanFormat(disk.getUsable()), pb));
				((AbstractOrderedLayout) pb.getParent()).setComponentAlignment(pb, Alignment.MIDDLE_CENTER);
				diskLayout.addComponent(itemLayout);
				break;
			case ERROR:
				diskLayout.addComponent(new ErrorMonitorDisplay("Chyba disku"));
				break;
			case UNAVAILABLE:
			default:
				diskLayout.addComponent(new WarningMonitorDisplay(disk.getName() + " info není dostupné"));
			}
		}
	}

	@Override
	protected Component createContent() {
		layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(true);

		JavaScript.getCurrent().addFunction("cz.gattserver.grass3.monitorupadate", new JavaScriptFunction() {
			private static final long serialVersionUID = 5850638851716815161L;

			@Override
			public void call(JsonArray arguments) {
				layout.removeAllComponents();
				populateMonitor();
			}
		});

		// update každé 5s
		JavaScript.eval("setInterval(function(){ cz.gattserver.grass3.monitorupadate(); }, 5000);");

		populateMonitor();

		return layout;
	}

	private void populateMonitor() {

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
	}
}
