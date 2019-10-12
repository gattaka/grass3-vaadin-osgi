package cz.gattserver.grass3.medic.dto;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import cz.gattserver.grass3.medic.util.MedicUtil;

public class MedicUtilTest {

	@Test
	public void test() throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		ScheduledVisitDTO dto = new ScheduledVisitDTO();

		String dateStart = "21.12.2012 09:29:58";
		String dateStop = "02.01.2013 10:31:48";

		dto.setDateTime(LocalDateTime.parse(dateStop, formatter));
		assertFalse(MedicUtil.fromNowAfter7Days(dto, LocalDateTime.parse(dateStart, formatter)));

		dateStart = "26.12.2012 09:29:58";
		dateStop = "02.01.2013 10:31:48";

		dto.setDateTime(LocalDateTime.parse(dateStop, formatter));
		assertTrue(MedicUtil.fromNowAfter7Days(dto, LocalDateTime.parse(dateStart, formatter)));

	}

}
