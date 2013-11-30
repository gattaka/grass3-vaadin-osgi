package test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitState;

public class StateTest {

	@Test
	public void test() {

		assertTrue(ScheduledVisitState.MISSED
				.compareTo(ScheduledVisitState.PLANNED) > 0);

		assertTrue(ScheduledVisitState.MISSED
				.compareTo(ScheduledVisitState.TO_BE_PLANNED) > 0);

		assertTrue(ScheduledVisitState.TO_BE_PLANNED
				.compareTo(ScheduledVisitState.PLANNED) > 0);

	}

}
