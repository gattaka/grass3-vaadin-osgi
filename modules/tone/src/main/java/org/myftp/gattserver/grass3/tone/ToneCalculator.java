package org.myftp.gattserver.grass3.tone;

import java.util.HashSet;
import java.util.Set;

public class ToneCalculator {

	public Set<Tone> createChord(Tone base, ChordType type) {
		Set<Tone> chordSet = new HashSet<Tone>();
		
		for (int offset : type.getToneOffsets()) {
			chordSet.add(Tone.tones[offset]);
		}
		
		return chordSet;
	}
	
}
