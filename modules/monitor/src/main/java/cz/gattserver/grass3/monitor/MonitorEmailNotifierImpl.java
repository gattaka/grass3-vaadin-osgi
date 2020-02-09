package cz.gattserver.grass3.monitor;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.monitor.facade.MonitorFacade;
import cz.gattserver.grass3.monitor.processor.item.LastBackupTimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.MonitorState;
import cz.gattserver.grass3.monitor.processor.item.SMARTMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.ServerServiceMonitorItemTO;
import cz.gattserver.grass3.services.MailService;

@Component
public class MonitorEmailNotifierImpl extends TimerTask implements MonitorEmailNotifier {

	private static Logger logger = LoggerFactory.getLogger(MonitorEmailNotifierImpl.class);

	@Autowired
	private MonitorFacade monitorFacade;

	@Autowired
	private MailService mailService;

	@Override
	public void run() {
		logger.info("Monitor TimerTask byl spuštěn");

		// Test, zda jsou nahozené systémy serveru
		for (ServerServiceMonitorItemTO to : monitorFacade.getServerServicesStatus()) {
			if (!MonitorState.SUCCESS.equals(to.getMonitorState()))
				mailService.sendToAdmin("GRASS3 Monitor oznámení o změně stavu monitorovaného předmětu",
						"Server služba " + to.getName() + " není aktivní nebo se nezdařilo zjistit její stav");
		}

		// Test, zda je připojen backup disk
		if (!MonitorState.SUCCESS.equals(monitorFacade.getBackupDiskMounted().getMonitorState())) {
			mailService.sendToAdmin("GRASS3 Monitor oznámení o změně stavu monitorovaného předmětu",
					"Backup disk není připojen nebo se nezdařilo zjistit jeho stav");
		}

		// Test, zda jsou prováděny pravidelně zálohy
		for (LastBackupTimeMonitorItemTO to : monitorFacade.getLastTimeOfBackup()) {
			if (!MonitorState.SUCCESS.equals(to.getMonitorState()))
				mailService.sendToAdmin("GRASS3 Monitor oznámení o změně stavu monitorovaného předmětu", to.getValue()
						+ " Záloha nebyla provedena, je starší než 24h nebo se nezdařilo zjistit její stav");
		}

		// Test, zda jsou disky dle SMART v pořádku
		for (SMARTMonitorItemTO to : monitorFacade.getSMARTInfo()) {
			if (MonitorState.UNAVAILABLE.equals(to.getMonitorState())) {
				mailService.sendToAdmin("GRASS3 Monitor oznámení o změně stavu monitorovaného předmětu",
						"Nezdařilo se zjistit stav SMART monitoru: " + to.getStateDetails());
				break;
			}
			if (MonitorState.ERROR.equals(to.getMonitorState())) {
				mailService.sendToAdmin("GRASS3 Monitor oznámení o změně stavu monitorovaného předmětu",
						"SMART monitor detekoval chyby");
				break;
			}
		}
	}

	@Override
	public TimerTask getTimerTask() {
		return this;
	}

}
