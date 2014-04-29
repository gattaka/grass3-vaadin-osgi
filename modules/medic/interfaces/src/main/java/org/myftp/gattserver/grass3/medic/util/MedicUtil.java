package org.myftp.gattserver.grass3.medic.util;

import java.util.Calendar;

import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;

public class MedicUtil {

	public static boolean isVisitPending(ScheduledVisitDTO dto) {
		int plannedMonth = Calendar.getInstance().get(Calendar.MONTH);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dto.getDate());
		int currentMonth = cal.get(Calendar.MONTH);
		return plannedMonth == currentMonth;
	}

}
