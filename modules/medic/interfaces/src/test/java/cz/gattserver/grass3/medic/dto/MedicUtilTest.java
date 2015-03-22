package cz.gattserver.grass3.medic.dto;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;

import cz.gattserver.grass3.medic.util.MedicUtil;

public class MedicUtilTest {

	@Test
	public void test() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		ScheduledVisitDTO dto = new ScheduledVisitDTO();

		String dateStart = "21.12.2012 09:29:58";
		String dateStop = "02.01.2013 10:31:48";

		dto.setDate(format.parse(dateStop));
		assertFalse(MedicUtil.fromNowAfter7Days(dto, format.parse(dateStart)));

		dateStart = "21.12.2012 09:29:58";
		dateStop = "04.01.2013 10:31:48";

		dto.setDate(format.parse(dateStop));
		assertTrue(MedicUtil.fromNowAfter7Days(dto, format.parse(dateStart)));
		
		dateStart = "21.12.2012 09:29:58";
		dateStop = "05.01.2013 10:31:48";

		dto.setDate(format.parse(dateStop));
		assertFalse(MedicUtil.fromNowAfter7Days(dto, format.parse(dateStart)));
	}

}
