package org.myftp.gattserver.grass3.tone;

public enum ChordType {

	Dur(new int[]{0,4,7}),
	Moll(new int[]{0,3,7}),
	Dur7(new int[]{0,4,7,10}),
	Moll7(new int[]{0,3,7,10}),
	Maj(new int[]{0,4,7,11}),
	Dim(new int[]{0,3,6,9}),
	Sus(new int[]{0,4,8});
	
	/**
	 * Zbylé tóny akordu vyjádřené jako posuv tónu po stupnici
	 */
	private int[] toneOffsets;

	private ChordType(int[] toneOffsets) {
		this.toneOffsets = toneOffsets;
	}

	public int[] getToneOffsets() {
		return toneOffsets;
	}
}
