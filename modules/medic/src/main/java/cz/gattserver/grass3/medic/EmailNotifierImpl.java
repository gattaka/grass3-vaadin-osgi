package cz.gattserver.grass3.medic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.util.MedicUtil;
import cz.gattserver.grass3.util.ServerMail;

@Component
public class EmailNotifierImpl extends TimerTask implements EmailNotifier {

	@Autowired
	private MedicFacade medicFacade;

	@Override
	public void run() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Date now = Calendar.getInstance().getTime();
		for (ScheduledVisitDTO to : medicFacade.getAllScheduledVisits()) {
			if (MedicUtil.fromNowAfter7Days(to, now)) {
				ServerMail.sendToAdmin("GRASS3 Medic oznámená o plánované události",
						"Událost naplánovaná na: " + dateFormat.format(to.getDate())
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
