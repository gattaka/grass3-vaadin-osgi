package cz.gattserver.grass3.monitor.web;

import java.text.NumberFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.monitor.MonitorEmailNotifier;
import cz.gattserver.grass3.monitor.facade.MonitorFacade;
import cz.gattserver.grass3.monitor.processor.item.BackupDiskMountedMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskStatusMonitorItemTO;
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
import cz.gattserver.grass3.monitor.web.label.ErrorMonitorDisplay;
import cz.gattserver.grass3.monitor.web.label.ErrorMonitorStateLabel;
import cz.gattserver.grass3.monitor.web.label.MonitorOutputLabel;
import cz.gattserver.grass3.monitor.web.label.SuccessMonitorDisplay;
import cz.gattserver.grass3.monitor.web.label.SuccessMonitorStateLabel;
import cz.gattserver.grass3.monitor.web.label.WarningMonitorDisplay;
import cz.gattserver.grass3.monitor.web.label.WarningMonitorStateLabel;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.ui.H2Label;

public class MonitorPage extends OneColumnPage {

	private static final int REFRESH_TIMEOUT = 5000;

	@Autowired
	private MonitorFacade monitorFacade;

	@Autowired
	private MonitorEmailNotifier emailNotifier;

	private VerticalLayout layout;

	public MonitorPage(GrassRequest request) {
		super(request);
	}

	private VerticalLayout populateMonitorPart(String caption, VerticalLayout partLayout) {
		partLayout.setMargin(true);
		partLayout.setSpacing(false);
		partLayout.addComponent(new H2Label(caption));
		return partLayout;
	}

	private String humanFormat(long value) {
		return HumanBytesSizeFormatter.format(value, false);
	}

	private void createServerServices(VerticalLayout serverServicesLayout) {
		populateMonitorPart("Server services", serverServicesLayout);
		for (ServerServiceMonitorItemTO to : monitorFacade.getServerServicesStatus()) {
			String content = to.getName() + " (<a target='_blank' href='" + to.getAddress() + "'>" + to.getAddress()
					+ "</a>) [status: " + to.getResponseCode() + "]";
			switch (to.getMonitorState()) {
			case SUCCESS:
				serverServicesLayout.addComponent(new SuccessMonitorDisplay(content));
				break;
			case UNAVAILABLE:
				serverServicesLayout.addComponent(new WarningMonitorDisplay(content));
				break;
			case ERROR:
			default:
				serverServicesLayout.addComponent(new ErrorMonitorDisplay(content));
			}
		}
	}

	private String constructUsedTotalFreeDescription(long used, float ratio, long total, long free) {
		return "obsazeno " + humanFormat(used) + " (" + NumberFormat.getIntegerInstance().format(ratio * 100) + "%) z "
				+ humanFormat(total) + "; volno " + humanFormat(free);
	}

	private void createSystemPart(VerticalLayout systemLayout) {
		populateMonitorPart("System", systemLayout);

		/*
		 * Uptime
		 */
		SystemUptimeMonitorItemTO uptimeTO = monitorFacade.getSystemUptime();
		switch (uptimeTO.getMonitorState()) {
		case SUCCESS:
			systemLayout.addComponent(new SuccessMonitorDisplay(uptimeTO.getValue()));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			systemLayout.addComponent(new WarningMonitorDisplay("System uptime info není dostupné"));
		}

		/*
		 * Memory
		 */
		SystemMemoryMonitorItemTO memoryTO = monitorFacade.getSystemMemoryStatus();
		switch (memoryTO.getMonitorState()) {
		case SUCCESS:
			systemLayout
					.addComponent(constructProgressMonitor(memoryTO.getUsedRation(), constructUsedTotalFreeDescription(
							memoryTO.getUsed(), memoryTO.getUsedRation(), memoryTO.getTotal(), memoryTO.getFree())));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			systemLayout.addComponent(new WarningMonitorDisplay("System memory info není dostupné"));
		}

		/*
		 * Swap
		 */
		SystemSwapMonitorItemTO swapTO = monitorFacade.getSystemSwapStatus();
		switch (swapTO.getMonitorState()) {
		case SUCCESS:
			systemLayout.addComponent(
					constructProgressMonitor(swapTO.getUsedRation(), constructUsedTotalFreeDescription(swapTO.getUsed(),
							swapTO.getUsedRation(), swapTO.getTotal(), swapTO.getFree())));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			systemLayout.addComponent(new WarningMonitorDisplay("System swap info není dostupné"));
		}
	}

	private void createJVMOverviewPart(VerticalLayout jvmOverviewLayout) {
		populateMonitorPart("JVM Overview", jvmOverviewLayout);

		/*
		 * JVM Uptime
		 */
		JVMUptimeMonitorItemTO uptimeTO = monitorFacade.getJVMUptime();
		switch (uptimeTO.getMonitorState()) {
		case SUCCESS:
			jvmOverviewLayout.addComponent(new SuccessMonitorDisplay(
					String.format("JVM uptime: %d days, %d hours, %d minutes, %d seconds%n", uptimeTO.getElapsedDays(),
							uptimeTO.getElapsedHours(), uptimeTO.getElapsedMinutes(), uptimeTO.getElapsedSeconds())));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmOverviewLayout.addComponent(new WarningMonitorDisplay("JVM uptime info není dostupné"));
		}

		/*
		 * JVM PID
		 */
		JVMPIDMonitorItemTO pidTO = monitorFacade.getJVMPID();
		switch (pidTO.getMonitorState()) {
		case SUCCESS:
			jvmOverviewLayout.addComponent(new SuccessMonitorDisplay("JVM PID: " + pidTO.getPid()));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmOverviewLayout.addComponent(new WarningMonitorDisplay("JVM PID info není dostupné"));
		}

		/*
		 * JVM Threads
		 */
		JVMThreadsMonitorItemTO threadsTO = monitorFacade.getJVMThreads();
		switch (threadsTO.getMonitorState()) {
		case SUCCESS:
			jvmOverviewLayout.addComponent(new SuccessMonitorDisplay(
					"Aktuální stav vláken: " + threadsTO.getCount() + " peak: " + threadsTO.getPeak()));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmOverviewLayout.addComponent(new WarningMonitorDisplay("JVM thread info není dostupné"));
		}

		/*
		 * JVM Memory
		 */
		JVMMemoryMonitorItemTO memoryTO = monitorFacade.getJVMMemory();
		switch (memoryTO.getMonitorState()) {
		case SUCCESS:
			float usedRatio = memoryTO.getUsedMemory() / (float) memoryTO.getTotalMemory();
			jvmOverviewLayout.addComponent(constructProgressMonitor(usedRatio, constructUsedTotalFreeDescription(
					memoryTO.getUsedMemory(), usedRatio, memoryTO.getTotalMemory(), memoryTO.getFreeMemory())));
			jvmOverviewLayout
					.addComponent(new SuccessMonitorDisplay("Max memory: " + humanFormat(memoryTO.getMaxMemory())));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmOverviewLayout.addComponent(new WarningMonitorDisplay("JVM thread info není dostupné"));
		}

	}

	private void createBackupPart(VerticalLayout backupLayout) {
		populateMonitorPart("Backup", backupLayout);
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

	private HorizontalLayout constructProgressMonitor(float ration, String description) {
		HorizontalLayout itemLayout = new HorizontalLayout();
		itemLayout.setSpacing(true);
		ProgressBar pb = new ProgressBar();
		pb.setValue(ration);
		pb.setWidth("200px");
		itemLayout.addComponent(new SuccessMonitorDisplay(description, pb));
		((AbstractOrderedLayout) pb.getParent()).setComponentAlignment(pb, Alignment.MIDDLE_CENTER);
		return itemLayout;
	}

	private VerticalLayout createMarginRightWrapper(Component c) {
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(new MarginInfo(false, true, false, false));
		vl.addComponent(c);
		vl.setWidth(null);
		return vl;
	}

	private void createDisksPart(VerticalLayout diskLayout) {
		populateMonitorPart("Disk status", diskLayout);
		List<DiskStatusMonitorItemTO> disks = monitorFacade.getDiskStatus();
		if (disks.isEmpty()) {
			diskLayout.addComponent(new WarningMonitorDisplay("Info není dostupné"));
			return;
		}
		GridLayout grid = new GridLayout(12, disks.size() + 1);
		grid.setWidth("100%");
		grid.setSpacing(false);
		diskLayout.addComponent(grid);

		int line = 0;
		int col = 0;

		col++;
		grid.addComponent(createMarginRightWrapper(new MonitorOutputLabel("Stav")), col++, line);
		grid.addComponent(createMarginRightWrapper(new MonitorOutputLabel("Mount")), col++, line);
		grid.addComponent(createMarginRightWrapper(new MonitorOutputLabel("Název")), col++, line);
		grid.addComponent(createMarginRightWrapper(new MonitorOutputLabel("FS Typ")), col++, line);
		grid.addComponent(createMarginRightWrapper(new MonitorOutputLabel("Volno")), col, line, col + 1, line);
		col += 2;
		grid.addComponent(createMarginRightWrapper(new MonitorOutputLabel("Obsazeno")), col, line, col + 1, line);
		col += 2;
		grid.addComponent(createMarginRightWrapper(new MonitorOutputLabel("Velikost")), col, line, col + 1, line);
		col += 2;
		Label spacer = new Label();
		spacer.setWidth("100%");
		grid.addComponent(spacer, col++, line);
		grid.setColumnExpandRatio(grid.getColumns() - 1, 1);

		line++;
		col = 0;
		for (DiskStatusMonitorItemTO disk : disks) {
			switch (disk.getMonitorState()) {
			case SUCCESS:
				grid.addComponent(createMarginRightWrapper(new SuccessMonitorStateLabel()), col++, line);
				ProgressBar pb = new ProgressBar();
				pb.setValue(disk.getUsedRation());
				pb.setWidth("200px");
				VerticalLayout pbLayout = createMarginRightWrapper(pb);
				pbLayout.setComponentAlignment(pb, Alignment.MIDDLE_LEFT);
				grid.addComponent(pbLayout, col++, line);
				grid.addComponent(createMarginRightWrapper(new MonitorOutputLabel(disk.getMount())), col++, line);
				grid.addComponent(createMarginRightWrapper(new MonitorOutputLabel(disk.getName())), col++, line);
				grid.addComponent(createMarginRightWrapper(new MonitorOutputLabel(disk.getType())), col++, line);

				String usableInfo[] = humanFormat(disk.getUsable()).split(" ");
				MonitorOutputLabel usableLabel = new MonitorOutputLabel(usableInfo[0]);
				VerticalLayout usableLayout = createMarginRightWrapper(usableLabel);
				grid.addComponent(usableLayout, col++, line);
				grid.setComponentAlignment(usableLayout, Alignment.BOTTOM_RIGHT);
				grid.addComponent(createMarginRightWrapper(new MonitorOutputLabel(usableInfo[1])), col++, line);

				String usedInfo[] = humanFormat(disk.getUsed()).split(" ");
				MonitorOutputLabel usedLabel = new MonitorOutputLabel(usedInfo[0]);
				VerticalLayout usedLayout = createMarginRightWrapper(usedLabel);
				grid.addComponent(usedLayout, col++, line);
				grid.setComponentAlignment(usedLayout, Alignment.BOTTOM_RIGHT);
				grid.addComponent(createMarginRightWrapper(new MonitorOutputLabel(usedInfo[1])), col++, line);

				String totalInfo[] = humanFormat(disk.getTotal()).split(" ");
				MonitorOutputLabel totalLabel = new MonitorOutputLabel(totalInfo[0]);
				VerticalLayout totalLayout = createMarginRightWrapper(totalLabel);
				grid.addComponent(totalLayout, col++, line);
				grid.setComponentAlignment(totalLayout, Alignment.BOTTOM_RIGHT);
				grid.addComponent(createMarginRightWrapper(new MonitorOutputLabel(totalInfo[1])), col++, line);
				break;
			case ERROR:
				grid.addComponent(new ErrorMonitorStateLabel(), col++, line);
				grid.addComponent(new MonitorOutputLabel("Chyba disku"), col, line);
				break;
			case UNAVAILABLE:
			default:
				grid.addComponent(new WarningMonitorStateLabel(), col++, line);
				grid.addComponent(new MonitorOutputLabel(disk.getName() + " info není dostupné"), col, line);
			}
			line++;
			col = 0;
		}
	}

	@Override
	protected Component createContent() {
		layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(true);

		populateMonitor();

		return layout;
	}

	private static interface RefreshedPart {
		void populate(VerticalLayout partLayout);
	}

	private void createRefreshedPart(String refFuncId, RefreshedPart populateMethod) {
		VerticalLayout partLayout = new VerticalLayout();
		layout.addComponent(partLayout);
		JavaScript.getCurrent().addFunction("cz.gattserver.grass3.monitor." + refFuncId, (arguments) -> {
			partLayout.removeAllComponents();
			populateMethod.populate(partLayout);
		});
		JavaScript.eval("setInterval(function(){ cz.gattserver.grass3.monitor." + refFuncId + "(); }, "
				+ REFRESH_TIMEOUT + ");");
		populateMethod.populate(partLayout);
	}

	private void populateMonitor() {

		// Server services
		createRefreshedPart("createServerServices", (partLayout) -> createServerServices(partLayout));

		// System
		createRefreshedPart("createSystemPart", (partLayout) -> createSystemPart(partLayout));

		// Úložiště
		createRefreshedPart("createDisksPart", (partLayout) -> createDisksPart(partLayout));

		// Backup disk
		createRefreshedPart("createBackupPart", (partLayout) -> createBackupPart(partLayout));

		// JVM Overview
		createRefreshedPart("createJVMOverviewPart", (partLayout) -> createJVMOverviewPart(partLayout));

		// JVM Heap
		// VerticalLayout jvmHeapLayout = new VerticalLayout();
		// layout.addComponent(jvmHeapLayout);
		// createJVMHeapPart(jvmHeapLayout);

		// Mail test
		VerticalLayout mailLayout = new VerticalLayout();
		layout.addComponent(mailLayout);
		populateMonitorPart("Mail notifier", mailLayout);
		Button testMailBtn = new Button("Mail notifier", e -> {
			emailNotifier.getTimerTask().run();
		});
		mailLayout.addComponent(testMailBtn);
	}
}
