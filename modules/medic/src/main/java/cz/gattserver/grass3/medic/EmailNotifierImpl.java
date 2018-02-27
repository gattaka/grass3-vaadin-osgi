package cz.gattserver.grass3.medic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.util.MedicUtil;
import cz.gattserver.grass3.services.MailService;

@Component
public class EmailNotifierImpl extends TimerTask implements EmailNotifier {

	@Autowired
	private MedicFacade medicFacade;

	@Autowired
	private MailService mailService;

	@Override
	public void run() {
		for (ScheduledVisitDTO to : medicFacade.getAllScheduledVisits()) {
			if (MedicUtil.fromNowAfter7Days(to, LocalDateTime.now())) {
				mailService.sendToAdmin("GRASS3 Medic oznámená o plánované události",
						"Událost naplánovaná na: "
								+ to.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
								+ " se blíží (nastane v příštích 7 dnech):\n\n" + "Instituce:\t"
								+ to.getInstitution().toString() + "\nDůvod návštěvy:\t" + to.getPurpose());
			}
		}
	}

	@Override
	public TimerTask getTimerTask() {
		return this;
	}

}
