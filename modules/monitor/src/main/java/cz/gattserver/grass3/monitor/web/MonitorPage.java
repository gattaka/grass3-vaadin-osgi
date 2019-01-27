package cz.gattserver.grass3.monitor.web;

import java.text.NumberFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.monitor.MonitorEmailNotifier;
import cz.gattserver.grass3.monitor.facade.MonitorFacade;
import cz.gattserver.grass3.monitor.processor.item.BackupDiskMountedMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskMountsMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskStatusMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMHeapMonitorItemTO;
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
import cz.gattserver.grass3.monitor.web.label.WarningMonitorDisplay;
import cz.gattserver.grass3.monitor.web.label.SuccessMonitorDisplay;
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
			HorizontalLayout itemLayout = new HorizontalLayout();
			itemLayout.setSpacing(true);
			String usedPerc = NumberFormat.getIntegerInstance().format(memoryTO.getUsedRation() * 100) + "%";
			ProgressBar pb = new ProgressBar();
			pb.setValue(memoryTO.getUsedRation());
			pb.setWidth("200px");
			itemLayout.addComponent(new SuccessMonitorDisplay(
					"obsazeno " + humanFormat(memoryTO.getUsed()) + " (" + usedPerc + ") z "
							+ humanFormat(memoryTO.getTotal()) + "; volno " + humanFormat(memoryTO.getAvailable()),
					pb));
			((AbstractOrderedLayout) pb.getParent()).setComponentAlignment(pb, Alignment.MIDDLE_CENTER);
			systemLayout.addComponent(itemLayout);
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
			HorizontalLayout itemLayout = new HorizontalLayout();
			itemLayout.setSpacing(true);
			String usedPerc = NumberFormat.getIntegerInstance().format(swapTO.getUsedRation() * 100) + "%";
			ProgressBar pb = new ProgressBar();
			pb.setValue(swapTO.getUsedRation());
			pb.setWidth("200px");
			itemLayout.addComponent(
					new SuccessMonitorDisplay("obsazeno " + humanFormat(swapTO.getUsed()) + " (" + usedPerc + ") z "
							+ humanFormat(swapTO.getTotal()) + "; volno " + humanFormat(swapTO.getFree()), pb));
			((AbstractOrderedLayout) pb.getParent()).setComponentAlignment(pb, Alignment.MIDDLE_CENTER);
			systemLayout.addComponent(itemLayout);
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
	}

	private void createJVMHeapPart(VerticalLayout jvmHeapLayout) {
		populateMonitorPart("JVM Heap", jvmHeapLayout);
		JVMHeapMonitorItemTO heapTO = monitorFacade.getJVMHeap();
		switch (heapTO.getMonitorState()) {
		case SUCCESS:
			jvmHeapLayout.addComponent(new SuccessMonitorDisplay("JVM Heap"));
			Grid<JVMHeapMonitorItemTO.Line> grid = new Grid<>(null, heapTO.getLines());
			grid.addColumn(JVMHeapMonitorItemTO.Line::getNum).setCaption("Pořadí");
			Column<JVMHeapMonitorItemTO.Line, String> nameColumn = grid.addColumn(JVMHeapMonitorItemTO.Line::getName)
					.setCaption("Třída");
			grid.addColumn(JVMHeapMonitorItemTO.Line::getInstances).setCaption("Instance");
			grid.addColumn(JVMHeapMonitorItemTO.Line::getBytes).setCaption("Byty");
			grid.setWidth("100%");
			grid.setHeight("300px");

			HeaderRow filteringHeader = grid.appendHeaderRow();

			// Obsah
			TextField contentFilterField = new TextField();
			contentFilterField.addStyleName(ValoTheme.TEXTFIELD_TINY);
			contentFilterField.setWidth("100%");
			contentFilterField.addValueChangeListener(e -> {
				grid.setItems(heapTO.getLines().stream().filter(
						i -> i.getName().matches(contentFilterField.getValue().replaceAll("\\*", ".*") + ".*")));
			});
			filteringHeader.getCell(nameColumn).setComponent(contentFilterField);

			jvmHeapLayout.addComponent(grid);
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmHeapLayout.addComponent(new WarningMonitorDisplay("JVM Heap info není dostupné"));
		}
	}

	private void createMountsPart(VerticalLayout mountsLayout) {
		populateMonitorPart("Mount points", mountsLayout);
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

	private void createDisksPart(VerticalLayout diskLayout) {
		populateMonitorPart("Disk status", diskLayout);
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

		// Mount points
		createRefreshedPart("createMountsPart", (partLayout) -> createMountsPart(partLayout));

		// Úložiště
		createRefreshedPart("createDisksPart", (partLayout) -> createDisksPart(partLayout));

		// Backup disk
		createRefreshedPart("createBackupPart", (partLayout) -> createBackupPart(partLayout));

		// JVM Overview
		createRefreshedPart("createJVMOverviewPart", (partLayout) -> createJVMOverviewPart(partLayout));

		// JVM Heap
		VerticalLayout jvmHeapLayout = new VerticalLayout();
		layout.addComponent(jvmHeapLayout);
		createJVMHeapPart(jvmHeapLayout);

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
