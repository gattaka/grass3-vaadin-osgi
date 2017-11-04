package cz.gattserver.grass3.monitor.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.monitor.facade.MonitorFacade;
import cz.gattserver.grass3.monitor.processor.ConsoleOutputTO;
import cz.gattserver.grass3.monitor.web.label.FAILMonitorItem;
import cz.gattserver.grass3.monitor.web.label.MonitorItemFactory;
import cz.gattserver.grass3.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

public class MonitorPage extends OneColumnPage {

	@Autowired
	private MonitorFacade monitorFacade;

	public MonitorPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(true);

		layout.addComponent(MonitorItemFactory.createMonitorItem(monitorFacade.getUptime()));

		ConsoleOutputTO mouted = monitorFacade.getBackupDiskMounted();
		if (monitorFacade.getBackupDiskMounted().isError() == false) {
			if (Boolean.parseBoolean(mouted.getOutput())) {
				layout.addComponent(MonitorItemFactory.createMonitorItem(monitorFacade.getBackupDiskSizeInfo()));

				layout.addComponent(MonitorItemFactory.createMonitorItem(monitorFacade.getLastTimeOfBackup()));
			} else {
				layout.addComponent(new FAILMonitorItem("Backup disk není připojen"));
			}
		}

		return layout;
	}
}
