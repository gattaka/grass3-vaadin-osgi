package cz.gattserver.grass3.monitor;

import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.monitor.facade.MonitorFacade;
import cz.gattserver.grass3.monitor.processor.item.MonitorState;
import cz.gattserver.grass3.services.MailService;

@Component
public class EmailNotifierImpl extends TimerTask implements EmailNotifier {

	@Autowired
	private MonitorFacade monitorFacade;

	@Autowired
	private MailService mailService;

	@Override
	public void run() {
		// Test, zda je připojen backup disk
		if (MonitorState.ERROR.equals(monitorFacade.getBackupDiskMounted())) {
			mailService.sendToAdmin("GRASS3 Monitor oznámení o změně stavu monitorovaného předmětu",
					"[ ERR! ] Backup disk není připojen");
		}

		// TODO test stáří záloh
	}

	@Override
	public TimerTask getTimerTask() {
		return this;
	}

}
