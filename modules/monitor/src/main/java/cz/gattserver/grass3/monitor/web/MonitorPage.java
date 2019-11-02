package cz.gattserver.grass3.monitor.web;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;

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
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.GridLayout;

@Route("system-monitor")
public class MonitorPage extends OneColumnPage {

	private static final long serialVersionUID = 4862261730750923131L;

	private static final int REFRESH_TIMEOUT = 5000;

	@Autowired
	private MonitorFacade monitorFacade;

	@Autowired
	private MonitorEmailNotifier emailNotifier;

	private VerticalLayout layout;

	private Set<Runnable> parts = new HashSet<>();

	public MonitorPage() {
		loadCSS(getContextPath() + "/frontend/monitor/style.css");
		init();
	}

	private VerticalLayout populateMonitorPart(String caption, VerticalLayout partLayout) {
		partLayout.setSpacing(false);
		partLayout.setPadding(false);
		partLayout.add(new H2(caption));
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
				serverServicesLayout.add(new SuccessMonitorDisplay(content));
				break;
			case UNAVAILABLE:
				serverServicesLayout.add(new WarningMonitorDisplay(content));
				break;
			case ERROR:
			default:
				serverServicesLayout.add(new ErrorMonitorDisplay(content));
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
			systemLayout.add(new SuccessMonitorDisplay(uptimeTO.getValue()));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			systemLayout.add(new WarningMonitorDisplay("System uptime info není dostupné"));
		}

		/*
		 * Memory
		 */
		SystemMemoryMonitorItemTO memoryTO = monitorFacade.getSystemMemoryStatus();
		switch (memoryTO.getMonitorState()) {
		case SUCCESS:
			systemLayout.add(constructProgressMonitor(memoryTO.getUsedRation(), constructUsedTotalFreeDescription(
					memoryTO.getUsed(), memoryTO.getUsedRation(), memoryTO.getTotal(), memoryTO.getFree())));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			systemLayout.add(new WarningMonitorDisplay("System memory info není dostupné"));
		}

		/*
		 * Swap
		 */
		SystemSwapMonitorItemTO swapTO = monitorFacade.getSystemSwapStatus();
		switch (swapTO.getMonitorState()) {
		case SUCCESS:
			systemLayout.add(constructProgressMonitor(swapTO.getUsedRation(), constructUsedTotalFreeDescription(
					swapTO.getUsed(), swapTO.getUsedRation(), swapTO.getTotal(), swapTO.getFree())));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			systemLayout.add(new WarningMonitorDisplay("System swap info není dostupné"));
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
			jvmOverviewLayout.add(new SuccessMonitorDisplay(
					String.format("JVM uptime: %d days, %d hours, %d minutes, %d seconds%n", uptimeTO.getElapsedDays(),
							uptimeTO.getElapsedHours(), uptimeTO.getElapsedMinutes(), uptimeTO.getElapsedSeconds())));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmOverviewLayout.add(new WarningMonitorDisplay("JVM uptime info není dostupné"));
		}

		/*
		 * JVM PID
		 */
		JVMPIDMonitorItemTO pidTO = monitorFacade.getJVMPID();
		switch (pidTO.getMonitorState()) {
		case SUCCESS:
			jvmOverviewLayout.add(new SuccessMonitorDisplay("JVM PID: " + pidTO.getPid()));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmOverviewLayout.add(new WarningMonitorDisplay("JVM PID info není dostupné"));
		}

		/*
		 * JVM Threads
		 */
		JVMThreadsMonitorItemTO threadsTO = monitorFacade.getJVMThreads();
		switch (threadsTO.getMonitorState()) {
		case SUCCESS:
			jvmOverviewLayout.add(new SuccessMonitorDisplay(
					"Aktuální stav vláken: " + threadsTO.getCount() + " peak: " + threadsTO.getPeak()));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmOverviewLayout.add(new WarningMonitorDisplay("JVM thread info není dostupné"));
		}

		/*
		 * JVM Memory
		 */
		JVMMemoryMonitorItemTO memoryTO = monitorFacade.getJVMMemory();
		switch (memoryTO.getMonitorState()) {
		case SUCCESS:
			float usedRatio = memoryTO.getUsedMemory() / (float) memoryTO.getTotalMemory();
			jvmOverviewLayout
					.add(constructProgressMonitor(usedRatio, constructUsedTotalFreeDescription(memoryTO.getUsedMemory(),
							usedRatio, memoryTO.getTotalMemory(), memoryTO.getFreeMemory())));
			jvmOverviewLayout.add(new SuccessMonitorDisplay("Max memory: " + humanFormat(memoryTO.getMaxMemory())));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmOverviewLayout.add(new WarningMonitorDisplay("JVM thread info není dostupné"));
		}

	}

	private void createBackupPart(VerticalLayout backupLayout) {
		populateMonitorPart("Backup", backupLayout);
		BackupDiskMountedMonitorItemTO mouted = monitorFacade.getBackupDiskMounted();
		switch (mouted.getMonitorState()) {
		case SUCCESS:
			backupLayout.add(new SuccessMonitorDisplay("Backup disk je připojen"));
			break;
		case ERROR:
			backupLayout.add(new ErrorMonitorDisplay("Backup disk není připojen"));
			break;
		case UNAVAILABLE:
		default:
			backupLayout.add(new WarningMonitorDisplay("Backup disk info není dostupné"));
		}

		if (MonitorState.SUCCESS.equals(mouted.getMonitorState())) {
			List<LastBackupTimeMonitorItemTO> lastBackupTOs = monitorFacade.getLastTimeOfBackup();
			for (LastBackupTimeMonitorItemTO lastBackupTO : lastBackupTOs) {
				switch (lastBackupTO.getMonitorState()) {
				case SUCCESS:
					backupLayout.add(new SuccessMonitorDisplay(lastBackupTO.getValue()));
					break;
				case ERROR:
					backupLayout.add(new ErrorMonitorDisplay(
							lastBackupTO.getValue() + ": Nebyla provedena pravidelná záloha nebo je starší, než 24h"));
					break;
				case UNAVAILABLE:
				default:
					backupLayout.add(
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
		itemLayout.add(new SuccessMonitorDisplay(description, pb));
		itemLayout.setVerticalComponentAlignment(Alignment.END, pb);
		return itemLayout;
	}

	private VerticalLayout createMarginRightWrapper(Component c) {
		VerticalLayout vl = new VerticalLayout();
		vl.add(c);
		vl.setWidth(null);
		return vl;
	}

	private void createDisksPart(VerticalLayout diskLayout) {
		populateMonitorPart("Disk status", diskLayout);
		List<DiskStatusMonitorItemTO> disks = monitorFacade.getDiskStatus();
		if (disks.isEmpty()) {
			diskLayout.add(new WarningMonitorDisplay("Info není dostupné"));
			return;
		}
		GridLayout grid = new GridLayout();
		grid.setWidth("100%");
		diskLayout.add(grid);

		grid.add(createMarginRightWrapper(new MonitorOutputLabel("Stav")));
		grid.add(createMarginRightWrapper(new MonitorOutputLabel("Mount")));
		grid.add(createMarginRightWrapper(new MonitorOutputLabel("Název")));
		grid.add(createMarginRightWrapper(new MonitorOutputLabel("FS Typ")));
		grid.add(createMarginRightWrapper(new MonitorOutputLabel("Volno")));
		grid.add(createMarginRightWrapper(new MonitorOutputLabel("Obsazeno")));
		grid.add(createMarginRightWrapper(new MonitorOutputLabel("Velikost")));
		Span spacer = new Span();
		spacer.setWidth("100%");
		grid.add(spacer);

		for (DiskStatusMonitorItemTO disk : disks) {
			grid.newRow();
			switch (disk.getMonitorState()) {
			case SUCCESS:
				grid.add(createMarginRightWrapper(new SuccessMonitorStateLabel()));
				ProgressBar pb = new ProgressBar();
				pb.setValue(disk.getUsedRation());
				pb.setWidth("200px");
				VerticalLayout pbLayout = createMarginRightWrapper(pb);
				grid.add(pbLayout);
				grid.add(createMarginRightWrapper(new MonitorOutputLabel(disk.getMount())));
				grid.add(createMarginRightWrapper(new MonitorOutputLabel(disk.getName())));
				grid.add(createMarginRightWrapper(new MonitorOutputLabel(disk.getType())));

				String usableInfo[] = humanFormat(disk.getUsable()).split(" ");
				MonitorOutputLabel usableLabel = new MonitorOutputLabel(usableInfo[0] + " " + usableInfo[1]);
				VerticalLayout usableLayout = createMarginRightWrapper(usableLabel);
				grid.add(usableLayout);

				String usedInfo[] = humanFormat(disk.getUsed()).split(" ");
				MonitorOutputLabel usedLabel = new MonitorOutputLabel(usedInfo[0] + " " + usedInfo[1]);
				VerticalLayout usedLayout = createMarginRightWrapper(usedLabel);
				grid.add(usedLayout);

				String totalInfo[] = humanFormat(disk.getTotal()).split(" ");
				MonitorOutputLabel totalLabel = new MonitorOutputLabel(totalInfo[0] + " " + totalInfo[1]);
				VerticalLayout totalLayout = createMarginRightWrapper(totalLabel);
				grid.add(totalLayout);
				break;
			case ERROR:
				grid.add(new ErrorMonitorStateLabel());
				grid.add(new MonitorOutputLabel("Chyba disku"));
				break;
			case UNAVAILABLE:
			default:
				grid.add(new WarningMonitorStateLabel());
				grid.add(new MonitorOutputLabel(disk.getName() + " info není dostupné"));
			}
		}
	}

	@Override
	protected void createColumnContent(Div layout) {
		this.layout = new VerticalLayout();
		this.layout.setPadding(false);
		this.layout.addClassName("monitor-content");
		layout.add(this.layout);
		populateMonitor();
	}

	private void createRefreshedPart(Consumer<VerticalLayout> populateMethod) {
		VerticalLayout partLayout = new VerticalLayout();
		partLayout.setPadding(false);
		layout.add(partLayout);
		parts.add(() -> {
			partLayout.removeAll();
			populateMethod.accept(partLayout);
		});
	}

	private void populateMonitor() {
		// Server services
		createRefreshedPart(this::createServerServices);

		// System
		createRefreshedPart(this::createSystemPart);

		// Úložiště
		createRefreshedPart(this::createDisksPart);

		// Backup disk
		createRefreshedPart(this::createBackupPart);

		// JVM Overview
		createRefreshedPart(this::createJVMOverviewPart);

		// JVM Heap
		// VerticalLayout jvmHeapLayout = new VerticalLayout();
		// layout.addComponent(jvmHeapLayout);
		// createJVMHeapPart(jvmHeapLayout);

		String jsDivId = "monitor-js-div";
		Div jsDiv = new Div() {
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void monitorRefresh() {
				parts.stream().forEach(Runnable::run);
			}
		};
		jsDiv.setId(jsDivId);
		jsDiv.getStyle().set("display", "none");
		layout.add(jsDiv);

		parts.stream().forEach(Runnable::run);

		UI.getCurrent().getPage().executeJs("setInterval(function(){document.getElementById('" + jsDivId
				+ "').$server.monitorRefresh() }, " + REFRESH_TIMEOUT + ");");

		// Mail test
		VerticalLayout mailLayout = new VerticalLayout();
		layout.add(mailLayout);
		populateMonitorPart("Mail notifier", mailLayout);
		Button testMailBtn = new Button("Mail notifier", e -> {
			emailNotifier.getTimerTask().run();
		});
		mailLayout.add(testMailBtn);
	}
}
