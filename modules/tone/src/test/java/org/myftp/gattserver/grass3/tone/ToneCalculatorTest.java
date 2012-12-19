package org.myftp.gattserver.grass3.tone;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ToneCalculatorTest {

	@Test
	public void test() {
		ToneCalculator calculator = new ToneCalculator();

		Set<Tone> cDur = calculator.createChord(Tone.C, ChordType.Dur);
		Set<Tone> expected = new HashSet<Tone>();
		expected.add(Tone.C);
		expected.add(Tone.E);
		expected.add(Tone.G);
		assertEquals(expected, cDur);
	}
}
