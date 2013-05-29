package org.myftp.gattserver.grass3.hw.dto;

public enum HWItemState {

	NEW("Nový"), FIXED("Opraven"), FAULTY("Poruchový"), BROKEN("Nefunkční"), DISASSEMBLED("Rozebrán");

	private String name;

	private HWItemState(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
