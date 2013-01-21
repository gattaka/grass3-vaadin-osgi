package org.myftp.gattserver.grass3.tone;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ToneCalculatorTest {

	@Test
	public void createChord1() {
		ToneCalculator calculator = new ToneCalculator();

		Set<Tone> cDur = calculator.createChord(Tone.C, ChordType.Dur);
		Set<Tone> expected = new HashSet<Tone>();
		expected.add(Tone.C);
		expected.add(Tone.E);
		expected.add(Tone.G);
		assertEquals(expected, cDur);
	}
	
	@Test
	public void createChord2() {
		ToneCalculator calculator = new ToneCalculator();

		Set<Tone> aDur = calculator.createChord(Tone.A, ChordType.Dur);
		Set<Tone> expected = new HashSet<Tone>();
		expected.add(Tone.A);
		expected.add(Tone.Cis);
		expected.add(Tone.E);
		assertEquals(expected, aDur);
	}
}
