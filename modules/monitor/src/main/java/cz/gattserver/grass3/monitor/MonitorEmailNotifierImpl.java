package cz.gattserver.grass3.monitor;

import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.monitor.facade.MonitorFacade;
import cz.gattserver.grass3.monitor.processor.item.LastBackupTimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.MonitorState;
import cz.gattserver.grass3.services.MailService;

@Component
public class MonitorEmailNotifierImpl extends TimerTask implements MonitorEmailNotifier {

	@Autowired
	private MonitorFacade monitorFacade;

	@Autowired
	private MailService mailService;

	@Override
	public void run() {
		// Test, zda je připojen backup disk
		if (MonitorState.ERROR.equals(monitorFacade.getBackupDiskMounted().getMonitorState())) {
			mailService.sendToAdmin("GRASS3 Monitor oznámení o změně stavu monitorovaného předmětu",
					"[ ERR! ] Backup disk není připojen");
		}

		// Test, zda jsou prováděny pravidelně zálohy
		for (LastBackupTimeMonitorItemTO to : monitorFacade.getLastTimeOfBackup()) {
			if (MonitorState.ERROR.equals(to.getMonitorState())) {
				mailService.sendToAdmin("GRASS3 Monitor oznámení o změně stavu monitorovaného předmětu",
						"[ ERR! ] " + to.getValue() + " Záloha nebyla provedena nebo je starší než 24h");
			}
		}
	}

	@Override
	public TimerTask getTimerTask() {
		return this;
	}

}
