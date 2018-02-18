package cz.gattserver.grass3.medic.util;

import java.time.LocalDateTime;

import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;

public class MedicUtil {

	private MedicUtil() {
	}

	public static boolean isVisitPending(ScheduledVisitDTO dto) {
		LocalDateTime date = dto.getDate();
		LocalDateTime now = LocalDateTime.now();
		return date.getMonthValue() == now.getMonthValue() && date.getYear() == now.getYear();
	}

	public static boolean fromNowAfter7Days(ScheduledVisitDTO dto, LocalDateTime now) {
		return now.plusDays(7).isAfter(dto.getDate());
	}

}
