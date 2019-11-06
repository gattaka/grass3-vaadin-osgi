package cz.gattserver.grass3.monitor.web;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.monitor.MonitorEmailNotifier;
import cz.gattserver.grass3.monitor.MonitorSection;
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
import cz.gattserver.grass3.monitor.web.label.ErrorMonitorStateLabel;
import cz.gattserver.grass3.monitor.web.label.MonitorOutputLabel;
import cz.gattserver.grass3.monitor.web.label.SuccessMonitorStateLabel;
import cz.gattserver.grass3.monitor.web.label.WarningMonitorStateLabel;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.TableLayout;
import cz.gattserver.web.common.spring.SpringContextHelper;

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
		if (!SpringContextHelper.getBean(MonitorSection.class).isVisibleForRoles(getUser().getRoles()))
			throw new GrassPageException(403);
		loadCSS(getContextPath() + "/frontend/monitor/style.css");
		init();
	}

	private String humanFormat(long value) {
		return HumanBytesSizeFormatter.format(value, false);
	}

	private TableLayout preparePart(String header, VerticalLayout layout) {
		layout.add(new H2(header));
		TableLayout tableLayout = new TableLayout();
		tableLayout.getElement().setAttribute("class", "monitor-table");
		layout.add(tableLayout);
		return tableLayout;
	}

	private void createServerServices(VerticalLayout serverServicesLayout) {
		TableLayout tableLayout = preparePart("Server services", serverServicesLayout);
		for (ServerServiceMonitorItemTO to : monitorFacade.getServerServicesStatus()) {
			String content = to.getName();
			Anchor anchor = new Anchor(to.getAddress(), to.getAddress());
			anchor.setTarget("_blank");
			String response = "[status: " + to.getResponseCode() + "]";
			switch (to.getMonitorState()) {
			case SUCCESS:
				tableLayout.newRow().add(new SuccessMonitorStateLabel()).add(content).add(anchor).add(response);
				break;
			case UNAVAILABLE:
				tableLayout.newRow().add(new WarningMonitorStateLabel()).add(content).add(anchor).add(response);
				break;
			case ERROR:
			default:
				tableLayout.newRow().add(new ErrorMonitorStateLabel()).add(content);
			}
		}
	}

	private String constructUsedTotalFreeDescription(long used, float ratio, long total, long free) {
		return "obsazeno " + humanFormat(used) + " (" + NumberFormat.getIntegerInstance().format(ratio * 100) + "%) z "
				+ humanFormat(total) + "; volno " + humanFormat(free);
	}

	private void createSystemPart(VerticalLayout systemLayout) {
		TableLayout tableLayout = preparePart("System", systemLayout);

		/*
		 * Uptime
		 */
		SystemUptimeMonitorItemTO uptimeTO = monitorFacade.getSystemUptime();
		switch (uptimeTO.getMonitorState()) {
		case SUCCESS:
			tableLayout.newRow().add(new SuccessMonitorStateLabel()).add(uptimeTO.getValue());
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			tableLayout.newRow().add(new WarningMonitorStateLabel()).add("System uptime info není dostupné");
		}

		/*
		 * Memory
		 */
		SystemMemoryMonitorItemTO memoryTO = monitorFacade.getSystemMemoryStatus();
		switch (memoryTO.getMonitorState()) {
		case SUCCESS:
			tableLayout.newRow().add(new SuccessMonitorStateLabel())
					.add(constructProgressMonitor(memoryTO.getUsedRation(), constructUsedTotalFreeDescription(
							memoryTO.getUsed(), memoryTO.getUsedRation(), memoryTO.getTotal(), memoryTO.getFree())));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			tableLayout.newRow().add(new WarningMonitorStateLabel()).add("System memory info není dostupné");
		}

		/*
		 * Swap
		 */
		SystemSwapMonitorItemTO swapTO = monitorFacade.getSystemSwapStatus();
		switch (swapTO.getMonitorState()) {
		case SUCCESS:
			tableLayout.newRow().add(new SuccessMonitorStateLabel()).add(
					constructProgressMonitor(swapTO.getUsedRation(), constructUsedTotalFreeDescription(swapTO.getUsed(),
							swapTO.getUsedRation(), swapTO.getTotal(), swapTO.getFree())));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			tableLayout.newRow().add(new WarningMonitorStateLabel()).add("System swap info není dostupné");
		}
	}

	private void createJVMOverviewPart(VerticalLayout jvmOverviewLayout) {
		TableLayout tableLayout = preparePart("JVM Overview", jvmOverviewLayout);

		/*
		 * JVM Uptime
		 */
		JVMUptimeMonitorItemTO uptimeTO = monitorFacade.getJVMUptime();
		switch (uptimeTO.getMonitorState()) {
		case SUCCESS:
			tableLayout.newRow().add(new SuccessMonitorStateLabel())
					.add(String.format("JVM uptime: %d days, %d hours, %d minutes, %d seconds%n",
							uptimeTO.getElapsedDays(), uptimeTO.getElapsedHours(), uptimeTO.getElapsedMinutes(),
							uptimeTO.getElapsedSeconds()));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			tableLayout.newRow().add(new WarningMonitorStateLabel()).add("JVM uptime info není dostupné");
		}

		/*
		 * JVM PID
		 */
		JVMPIDMonitorItemTO pidTO = monitorFacade.getJVMPID();
		switch (pidTO.getMonitorState()) {
		case SUCCESS:
			tableLayout.newRow().add(new SuccessMonitorStateLabel()).add("JVM PID: " + pidTO.getPid());
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			tableLayout.newRow().add(new WarningMonitorStateLabel()).add("JVM PID info není dostupné");
		}

		/*
		 * JVM Threads
		 */
		JVMThreadsMonitorItemTO threadsTO = monitorFacade.getJVMThreads();
		switch (threadsTO.getMonitorState()) {
		case SUCCESS:
			tableLayout.newRow().add(new SuccessMonitorStateLabel())
					.add("Aktuální stav vláken: " + threadsTO.getCount() + " peak: " + threadsTO.getPeak());
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			tableLayout.newRow().add(new WarningMonitorStateLabel()).add("JVM thread info není dostupné");
		}

		/*
		 * JVM Memory
		 */
		JVMMemoryMonitorItemTO memoryTO = monitorFacade.getJVMMemory();
		switch (memoryTO.getMonitorState()) {
		case SUCCESS:
			float usedRatio = memoryTO.getUsedMemory() / (float) memoryTO.getTotalMemory();
			tableLayout.newRow().add(new SuccessMonitorStateLabel())
					.add(constructProgressMonitor(usedRatio, constructUsedTotalFreeDescription(memoryTO.getUsedMemory(),
							usedRatio, memoryTO.getTotalMemory(), memoryTO.getFreeMemory())));
			tableLayout.add(" Max memory: " + humanFormat(memoryTO.getMaxMemory()), false);
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			tableLayout.newRow().add(new WarningMonitorStateLabel()).add("JVM thread info není dostupné");
		}

	}

	private void createBackupPart(VerticalLayout backupLayout) {
		TableLayout tableLayout = preparePart("Backup", backupLayout);

		BackupDiskMountedMonitorItemTO mouted = monitorFacade.getBackupDiskMounted();
		switch (mouted.getMonitorState()) {
		case SUCCESS:
			tableLayout.newRow().add(new SuccessMonitorStateLabel()).add("Backup disk je připojen");
			break;
		case ERROR:
			tableLayout.newRow().add(new ErrorMonitorStateLabel()).add("Backup disk není připojen");
			break;
		case UNAVAILABLE:
		default:
			tableLayout.newRow().add(new WarningMonitorStateLabel()).add("Backup disk info není dostupné");
		}

		if (MonitorState.SUCCESS.equals(mouted.getMonitorState())) {
			List<LastBackupTimeMonitorItemTO> lastBackupTOs = monitorFacade.getLastTimeOfBackup();
			for (LastBackupTimeMonitorItemTO lastBackupTO : lastBackupTOs) {
				switch (lastBackupTO.getMonitorState()) {
				case SUCCESS:
					tableLayout.newRow().add(new SuccessMonitorStateLabel()).add(lastBackupTO.getValue());
					break;
				case ERROR:
					tableLayout.newRow().add(new ErrorMonitorStateLabel()).add(
							lastBackupTO.getValue() + ": Nebyla provedena pravidelná záloha nebo je starší, než 24h");
					break;
				case UNAVAILABLE:
				default:
					tableLayout.newRow().add(new WarningMonitorStateLabel())
							.add("Backup disk info o provedení poslední zálohy není dostupné");
				}
			}
		}

	}

	private ProgressBar constructProgressMonitor(float ration, String description) {
		ProgressBar pb = new ProgressBar();
		pb.setValue(ration);
		pb.setWidth("200px");
		return pb;
	}

	private void createDisksPart(VerticalLayout diskLayout) {
		TableLayout tableLayout = preparePart("Disk status", diskLayout);

		List<DiskStatusMonitorItemTO> disks = monitorFacade.getDiskStatus();
		if (disks.isEmpty()) {
			tableLayout.add(new WarningMonitorStateLabel()).add("Info není dostupné");
			return;
		}

		tableLayout.add(new MonitorOutputLabel("Stav")).setColSpan(2);
		tableLayout.add(new MonitorOutputLabel("Mount"));
		tableLayout.add(new MonitorOutputLabel("Název"));
		tableLayout.add(new MonitorOutputLabel("FS Typ"));
		tableLayout.add(new MonitorOutputLabel("Volno")).setColSpan(2);
		tableLayout.add(new MonitorOutputLabel("Obsazeno")).setColSpan(2);
		tableLayout.add(new MonitorOutputLabel("Velikost")).setColSpan(2);

		for (DiskStatusMonitorItemTO disk : disks) {
			tableLayout.newRow();
			switch (disk.getMonitorState()) {
			case SUCCESS:
				tableLayout.add(new SuccessMonitorStateLabel());
				ProgressBar pb = new ProgressBar();
				pb.setValue(disk.getUsedRation());
				pb.setWidth("200px");
				tableLayout.add(pb);
				tableLayout.add(disk.getMount());
				tableLayout.add(disk.getName());
				tableLayout.add(disk.getType());

				String usableInfo[] = humanFormat(disk.getUsable()).split(" ");
				tableLayout.add(usableInfo[0]);
				tableLayout.add(usableInfo[1]);

				String usedInfo[] = humanFormat(disk.getUsed()).split(" ");
				tableLayout.add(usedInfo[0]);
				tableLayout.add(usedInfo[1]);

				String totalInfo[] = humanFormat(disk.getTotal()).split(" ");
				tableLayout.add(totalInfo[0]);
				tableLayout.add(totalInfo[1]);
				break;
			case ERROR:
				tableLayout.add(new ErrorMonitorStateLabel());
				tableLayout.add(new MonitorOutputLabel("Chyba disku"));
				break;
			case UNAVAILABLE:
			default:
				tableLayout.add(new WarningMonitorStateLabel());
				tableLayout.add(new MonitorOutputLabel(disk.getName() + " info není dostupné"));
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
		partLayout.setSpacing(false);
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
		layout.add(new H2("Mail notifier"));
		Button testMailBtn = new Button("Mail notifier", e -> emailNotifier.getTimerTask().run());
		layout.add(testMailBtn);
	}
}
