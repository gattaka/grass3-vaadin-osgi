package cz.gattserver.grass3.medic.util;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;

public class MedicUtil {

	public static boolean isVisitPending(ScheduledVisitDTO dto) {
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dto.getDate());
		int plannedMonth = cal.get(Calendar.MONTH);
		int plannedYear = cal.get(Calendar.YEAR);
		return plannedMonth == currentMonth && plannedYear == currentYear;
	}

	public static boolean fromNowAfter7Days(ScheduledVisitDTO dto, Date now) {
		DateTime dateTime1 = new DateTime(now);
		DateTime dateTime2 = new DateTime(dto.getDate());
		return Days.daysBetween(dateTime1, dateTime2).getDays() == 7;
	}

}
