package cz.gattserver.grass3.monitor.web;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.monitor.MonitorEmailNotifier;
import cz.gattserver.grass3.monitor.MonitorSection;
import cz.gattserver.grass3.monitor.facade.MonitorFacade;
import cz.gattserver.grass3.monitor.processor.item.BackupStatusMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.BackupStatusPartItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskStatusMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskStatusPartItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMMemoryMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMPIDMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMThreadsMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMUptimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.MonitorState;
import cz.gattserver.grass3.monitor.processor.item.SMARTMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SMARTPartItemTO;
import cz.gattserver.grass3.monitor.processor.item.ServerServiceMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.ServerServicePartItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemMemoryMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemSwapMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemUptimeMonitorItemTO;
import cz.gattserver.grass3.monitor.web.label.ErrorMonitorStateLabel;
import cz.gattserver.grass3.monitor.web.label.MonitorOutputLabel;
import cz.gattserver.grass3.monitor.web.label.SuccessMonitorStateLabel;
import cz.gattserver.grass3.monitor.web.label.WarningMonitorStateLabel;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.TableLayout;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import elemental.json.JsonObject;
import elemental.json.JsonType;

@Route("system-monitor")
@PageTitle("System monitor")
public class MonitorPage extends OneColumnPage {

	private static final long serialVersionUID = 4862261730750923131L;

	@Autowired
	private MonitorFacade monitorFacade;

	@Autowired
	private MonitorEmailNotifier emailNotifier;

	private VerticalLayout layout;

	private VerticalLayout serverServicesLayout;

	private VerticalLayout uptimeLayout;
	private VerticalLayout memoryStatusLayout;
	private VerticalLayout systemSwapStatusLayout;

	private VerticalLayout jvmUptimeLayout;
	private VerticalLayout jvmPIDLayout;
	private VerticalLayout jvmThreadsLayout;
	private VerticalLayout jvmMemoryLayout;

	private VerticalLayout backupLayout;

	private VerticalLayout diskLayout;

	private VerticalLayout smartLayout;

	public MonitorPage() {
		if (!SpringContextHelper.getBean(MonitorSection.class).isVisibleForRoles(getUser().getRoles()))
			throw new GrassPageException(403);
		loadCSS(getContextPath() + "/frontend/monitor/style.css");
		init();
	}

	private String humanFormat(long value) {
		return HumanBytesSizeFormatter.format(value, false);
	}

	private String constructUsedTotalFreeDescription(long used, float ratio, long total, long free) {
		return "obsazeno " + humanFormat(used) + " (" + NumberFormat.getIntegerInstance().format(ratio * 100) + "%) z "
				+ humanFormat(total) + "; volno " + humanFormat(free);
	}

	private ProgressBar constructProgressMonitor(float ration, String description) {
		ProgressBar pb = new ProgressBar();
		pb.setValue(ration);
		pb.setWidth("200px");
		return pb;
	}

	private void preparePartHeader(String header) {
		layout.add(new H2(header));
	}

	private TableLayout prepareTableLayout() {
		TableLayout tableLayout = new TableLayout();
		tableLayout.getElement().setAttribute("class", "monitor-table");
		return tableLayout;
	}

	private VerticalLayout preparePartLayout() {
		VerticalLayout partLayout = new VerticalLayout();
		partLayout.setMargin(false);
		partLayout.setPadding(false);
		partLayout.setSpacing(false);
		Image loadingImg = new Image("img/gattload_mini.gif", "loading...");
		loadingImg.getStyle().set("margin", "5px");
		partLayout.add(loadingImg);
		layout.add(partLayout);
		return partLayout;
	}

	private VerticalLayout preparePart(String header) {
		preparePartHeader(header);
		return preparePartLayout();
	}

	private void createServerServices(ServerServicePartItemTO data) {
		serverServicesLayout.removeAll();
		TableLayout serverServicesTableLayout = prepareTableLayout();
		serverServicesLayout.add(serverServicesTableLayout);
		for (ServerServiceMonitorItemTO to : data.getItems()) {
			String content = to.getName();
			Anchor anchor = new Anchor(to.getAddress(), to.getAddress());
			anchor.setTarget("_blank");
			String response = "[status: " + to.getResponseCode() + "]";
			switch (to.getMonitorState()) {
			case SUCCESS:
				serverServicesTableLayout.newRow().add(new SuccessMonitorStateLabel()).add(content).add(anchor)
						.add(response);
				break;
			case UNAVAILABLE:
				serverServicesTableLayout.newRow().add(new WarningMonitorStateLabel()).add(content).add(anchor)
						.add(response);
				break;
			case ERROR:
			default:
				serverServicesTableLayout.newRow().add(new ErrorMonitorStateLabel()).add(content);
			}
		}
	}

	private void createSystemUptimePart(SystemUptimeMonitorItemTO uptimeTO) {
		uptimeLayout.removeAll();
		TableLayout uptimeTableLayout = prepareTableLayout();
		uptimeLayout.add(uptimeTableLayout);
		switch (uptimeTO.getMonitorState()) {
		case SUCCESS:
			uptimeTableLayout.newRow().add(new SuccessMonitorStateLabel()).add(uptimeTO.getValue());
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			uptimeTableLayout.newRow().add(new WarningMonitorStateLabel()).add("System uptime info není dostupné");
		}
	}

	private void createSystemMemoryStatusPart(SystemMemoryMonitorItemTO memoryTO) {
		memoryStatusLayout.removeAll();
		TableLayout memoryStatusTableLayout = prepareTableLayout();
		memoryStatusLayout.add(memoryStatusTableLayout);
		switch (memoryTO.getMonitorState()) {
		case SUCCESS:
			memoryStatusTableLayout.newRow().add(new SuccessMonitorStateLabel())
					.add(constructProgressMonitor(memoryTO.getUsedRation(), constructUsedTotalFreeDescription(
							memoryTO.getUsed(), memoryTO.getUsedRation(), memoryTO.getTotal(), memoryTO.getFree())));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			memoryStatusTableLayout.newRow().add(new WarningMonitorStateLabel())
					.add("System memory info není dostupné");
		}
	}

	private void createSystemSwapStatusPart(SystemSwapMonitorItemTO swapTO) {
		systemSwapStatusLayout.removeAll();
		TableLayout systemSwapStatusTableLayout = prepareTableLayout();
		systemSwapStatusLayout.add(systemSwapStatusTableLayout);
		swapTO = monitorFacade.getSystemSwapStatus();
		switch (swapTO.getMonitorState()) {
		case SUCCESS:
			systemSwapStatusTableLayout.newRow().add(new SuccessMonitorStateLabel()).add(
					constructProgressMonitor(swapTO.getUsedRation(), constructUsedTotalFreeDescription(swapTO.getUsed(),
							swapTO.getUsedRation(), swapTO.getTotal(), swapTO.getFree())));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			systemSwapStatusTableLayout.newRow().add(new WarningMonitorStateLabel())
					.add("System swap info není dostupné");
		}
	}

	private void createJVMUptimePart(JVMUptimeMonitorItemTO uptimeTO) {
		jvmUptimeLayout.removeAll();
		TableLayout jvmUptimeTableLayout = prepareTableLayout();
		jvmUptimeLayout.add(jvmUptimeTableLayout);
		switch (uptimeTO.getMonitorState()) {
		case SUCCESS:
			jvmUptimeTableLayout.newRow().add(new SuccessMonitorStateLabel())
					.add(String.format("JVM uptime: %d days, %d hours, %d minutes, %d seconds%n",
							uptimeTO.getElapsedDays(), uptimeTO.getElapsedHours(), uptimeTO.getElapsedMinutes(),
							uptimeTO.getElapsedSeconds()));
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmUptimeTableLayout.newRow().add(new WarningMonitorStateLabel()).add("JVM uptime info není dostupné");
		}
	}

	private void createJVMPIDPart(JVMPIDMonitorItemTO pidTO) {
		jvmPIDLayout.removeAll();
		TableLayout jvmPIDTableLayout = prepareTableLayout();
		jvmPIDLayout.add(jvmPIDTableLayout);
		switch (pidTO.getMonitorState()) {
		case SUCCESS:
			jvmPIDTableLayout.newRow().add(new SuccessMonitorStateLabel()).add("JVM PID: " + pidTO.getPid());
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmPIDTableLayout.newRow().add(new WarningMonitorStateLabel()).add("JVM PID info není dostupné");
		}
	}

	private void createJVMThreadsPart(JVMThreadsMonitorItemTO threadsTO) {
		jvmThreadsLayout.removeAll();
		TableLayout jvmThreadsTableLayout = prepareTableLayout();
		jvmThreadsLayout.add(jvmThreadsTableLayout);
		switch (threadsTO.getMonitorState()) {
		case SUCCESS:
			jvmThreadsTableLayout.newRow().add(new SuccessMonitorStateLabel())
					.add("Aktuální stav vláken: " + threadsTO.getCount() + " peak: " + threadsTO.getPeak());
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmThreadsTableLayout.newRow().add(new WarningMonitorStateLabel()).add("JVM thread info není dostupné");
		}
	}

	private void createJVMMemoryPart(JVMMemoryMonitorItemTO memoryTO) {
		jvmMemoryLayout.removeAll();
		TableLayout jvmMemoryTableLayout = prepareTableLayout();
		jvmMemoryLayout.add(jvmMemoryTableLayout);
		switch (memoryTO.getMonitorState()) {
		case SUCCESS:
			float usedRatio = memoryTO.getUsedMemory() / (float) memoryTO.getTotalMemory();
			jvmMemoryTableLayout.newRow().add(new SuccessMonitorStateLabel())
					.add(constructProgressMonitor(usedRatio, constructUsedTotalFreeDescription(memoryTO.getUsedMemory(),
							usedRatio, memoryTO.getTotalMemory(), memoryTO.getFreeMemory())));
			jvmMemoryTableLayout.add(" Max memory: " + humanFormat(memoryTO.getMaxMemory()), false);
			break;
		case UNAVAILABLE:
		case ERROR:
		default:
			jvmMemoryTableLayout.newRow().add(new WarningMonitorStateLabel()).add("JVM thread info není dostupné");
		}
	}

	private void createBackupStatusPart(BackupStatusPartItemTO backupPartItemTO) {
		backupLayout.removeAll();
		TableLayout backupTableLayout = prepareTableLayout();
		backupLayout.add(backupTableLayout);

		switch (backupPartItemTO.getMonitorState()) {
		case SUCCESS:
			backupTableLayout.newRow().add(new SuccessMonitorStateLabel()).add("Backup disk je připojen");
			break;
		case ERROR:
			backupTableLayout.newRow().add(new ErrorMonitorStateLabel()).add("Backup disk není připojen");
			break;
		case UNAVAILABLE:
		default:
			backupTableLayout.newRow().add(new WarningMonitorStateLabel()).add("Backup disk info není dostupné");
		}

		if (MonitorState.SUCCESS.equals(backupPartItemTO.getMonitorState())) {
			for (BackupStatusMonitorItemTO lastBackupTO : backupPartItemTO.getItems()) {
				switch (lastBackupTO.getMonitorState()) {
				case SUCCESS:
					backupTableLayout.newRow().add(new SuccessMonitorStateLabel()).add(lastBackupTO.getValue());
					break;
				case ERROR:
					backupTableLayout.newRow().add(new ErrorMonitorStateLabel()).add(
							lastBackupTO.getValue() + ": Nebyla provedena pravidelná záloha nebo je starší, než 24h");
					break;
				case UNAVAILABLE:
				default:
					backupTableLayout.newRow().add(new WarningMonitorStateLabel())
							.add("Backup disk info o provedení poslední zálohy není dostupné");
				}
			}
		}
	}

	private void createDisksPart(DiskStatusPartItemTO data) {
		diskLayout.removeAll();
		TableLayout diskTableLayout = prepareTableLayout();
		diskLayout.add(diskTableLayout);

		if (MonitorState.UNAVAILABLE == data.getMonitorState()) {
			diskTableLayout.add(new WarningMonitorStateLabel()).add("Info není dostupné");
			return;
		}

		diskTableLayout.add(new MonitorOutputLabel("Stav")).setColSpan(2);
		diskTableLayout.add(new MonitorOutputLabel("Mount"));
		diskTableLayout.add(new MonitorOutputLabel("Název"));
		diskTableLayout.add(new MonitorOutputLabel("FS Typ"));
		diskTableLayout.add(new MonitorOutputLabel("Volno")).setColSpan(2);
		diskTableLayout.add(new MonitorOutputLabel("Obsazeno")).setColSpan(2);
		diskTableLayout.add(new MonitorOutputLabel("Velikost")).setColSpan(2);

		for (DiskStatusMonitorItemTO disk : data.getItems()) {
			diskTableLayout.newRow();
			switch (disk.getMonitorState()) {
			case SUCCESS:
				diskTableLayout.add(new SuccessMonitorStateLabel());
				ProgressBar pb = new ProgressBar();
				pb.setValue(disk.getUsedRation());
				pb.setWidth("200px");
				diskTableLayout.add(pb);
				diskTableLayout.add(disk.getMount());
				diskTableLayout.add(disk.getName());
				diskTableLayout.add(disk.getType());

				String usableInfo[] = humanFormat(disk.getUsable()).split(" ");
				diskTableLayout.add(usableInfo[0]);
				diskTableLayout.add(usableInfo[1]);

				String usedInfo[] = humanFormat(disk.getUsed()).split(" ");
				diskTableLayout.add(usedInfo[0]);
				diskTableLayout.add(usedInfo[1]);

				String totalInfo[] = humanFormat(disk.getTotal()).split(" ");
				diskTableLayout.add(totalInfo[0]);
				diskTableLayout.add(totalInfo[1]);
				break;
			case ERROR:
				diskTableLayout.add(new ErrorMonitorStateLabel());
				diskTableLayout.add("Chyba disku");
				break;
			case UNAVAILABLE:
			default:
				diskTableLayout.add(new WarningMonitorStateLabel());
				diskTableLayout.add(disk.getName() + " info není dostupné");
			}
		}
	}

	private void createSMARTPart(SMARTPartItemTO data) {
		smartLayout.removeAll();
		TableLayout smartTableLayout = prepareTableLayout();
		smartLayout.add(smartTableLayout);

		// Vyžaduje být ve skupině
		// sudo usermod -a -G systemd-journal tomcat8
		for (SMARTMonitorItemTO to : data.getItems()) {
			// https://www.freedesktop.org/software/systemd/man/journalctl.html
			switch (to.getMonitorState()) {
			case SUCCESS:
				smartTableLayout.newRow().add(new SuccessMonitorStateLabel());
				smartTableLayout.add(to.getTime());
				smartTableLayout.add(to.getMessage());
				break;
			case ERROR:
				smartTableLayout.newRow().add(new ErrorMonitorStateLabel());
				smartTableLayout.add(to.getTime());
				smartTableLayout.add(to.getMessage());
				break;
			case UNAVAILABLE:
				smartTableLayout.newRow().add(new WarningMonitorStateLabel());
				smartTableLayout.add(to.getStateDetails());
				break;
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

	private void populateMonitor() {
		layout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);

		// Server services
		serverServicesLayout = preparePart("Server services");

		// System
		preparePartHeader("System");
		uptimeLayout = preparePartLayout();
		memoryStatusLayout = preparePartLayout();
		systemSwapStatusLayout = preparePartLayout();

		// Úložiště
		diskLayout = preparePart("Disk status");

		// Backup disk
		backupLayout = preparePart("Backup");

		// SMARTD
		smartLayout = preparePart("SMARTD");

		// JVM Overview
		preparePartHeader("JVM Overview");
		jvmUptimeLayout = preparePartLayout();
		jvmPIDLayout = preparePartLayout();
		jvmThreadsLayout = preparePartLayout();
		jvmMemoryLayout = preparePartLayout();

		String jsDivId = "monitor-js-div";
		Div jsDiv = new Div() {
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void monitorRefresh(JsonObject jsonObject) {
				System.out.println(jsonObject);
				if (!jsonObject.hasKey("type"))
					return;
				if (JsonType.NULL == jsonObject.get("type").getType())
					return;
				String type = jsonObject.getString("type");
				if (SystemUptimeMonitorItemTO.class.getName().equals(type)) {
					createSystemUptimePart(new SystemUptimeMonitorItemTO(jsonObject));
				} else if (SystemMemoryMonitorItemTO.class.getName().equals(type)) {
					createSystemMemoryStatusPart(new SystemMemoryMonitorItemTO(jsonObject));
				} else if (SystemSwapMonitorItemTO.class.getName().equals(type)) {
					createSystemSwapStatusPart(new SystemSwapMonitorItemTO(jsonObject));
				} else if (ServerServicePartItemTO.class.getName().equals(type)) {
					createServerServices(new ServerServicePartItemTO(jsonObject));
				} else if (BackupStatusPartItemTO.class.getName().equals(type)) {
					createBackupStatusPart(new BackupStatusPartItemTO(jsonObject));
				} else if (JVMMemoryMonitorItemTO.class.getName().equals(type)) {
					createJVMMemoryPart(new JVMMemoryMonitorItemTO(jsonObject));
				} else if (JVMPIDMonitorItemTO.class.getName().equals(type)) {
					createJVMPIDPart(new JVMPIDMonitorItemTO(jsonObject));
				} else if (JVMThreadsMonitorItemTO.class.getName().equals(type)) {
					createJVMThreadsPart(new JVMThreadsMonitorItemTO(jsonObject));
				} else if (JVMUptimeMonitorItemTO.class.getName().equals(type)) {
					createJVMUptimePart(new JVMUptimeMonitorItemTO(jsonObject));
				} else if (DiskStatusPartItemTO.class.getName().equals(type)) {
					createDisksPart(new DiskStatusPartItemTO(jsonObject));
				} else if (SMARTPartItemTO.class.getName().equals(type)) {
					createSMARTPart(new SMARTPartItemTO(jsonObject));
				}
			}
		};
		jsDiv.setId(jsDivId);
		jsDiv.getStyle().set("display", "none");
		layout.add(jsDiv);

		String url = UIUtils.getContextPath() + "/ws/system-monitor";

		Map<String, Integer> partsAndIntervals = new HashMap<>();
		partsAndIntervals.put("services-status", 10000);
		partsAndIntervals.put("system-uptime", 5000);
		partsAndIntervals.put("system-memory-status", 2000);
		partsAndIntervals.put("system-swap-status", 2000);
		partsAndIntervals.put("jvm-uptime", 5000);
		partsAndIntervals.put("jvm-pid", 10000);
		partsAndIntervals.put("jvm-threads", 5000);
		partsAndIntervals.put("jvm-memory", 2000);
		partsAndIntervals.put("backup-status", 10000);
		partsAndIntervals.put("disk-status", 10000);
		partsAndIntervals.put("smart-info", 10000);

		for (Entry<String, Integer> entry : partsAndIntervals.entrySet()) {
			String partUrl = url + "/" + entry.getKey();
			String partJS = "$.ajax({ url: \"" + partUrl + "\", type: \"GET\","
			/*								*/ + "data: \"\","
			/*								*/ + "beforeSend: function(xhr) {"
			/*									*/ + "xhr.setRequestHeader(\"Accept\", \"application/json\");"
			/*									*/ + "xhr.setRequestHeader(\"Content-Type\", \"application/json\");"
			/*								*/ + "},"
			/*								*/ + "success: function (data) {"
			/*									*/ + "document.getElementById('" + jsDivId
			/*									*/ + "').$server.monitorRefresh(data);"
			/*								*/ + "}"
			/*				*/ + "});";
			UI.getCurrent().getPage().executeJs("setTimeout(function(){" + partJS + "}," + 1000 + ");");
			UI.getCurrent().getPage().executeJs("setInterval(function(){" + partJS + "}," + entry.getValue() + ");");
		}

		// Mail test
		layout.add(new H2("Mail notifier"));
		Button testMailBtn = new Button("Mail notifier", e -> emailNotifier.getTimerTask().run());
		layout.add(testMailBtn);
	}
}
