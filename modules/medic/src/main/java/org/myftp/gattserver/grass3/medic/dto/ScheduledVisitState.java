package org.myftp.gattserver.grass3.medic.dto;

/**
 * Stavy plánované návštěvy
 */
public enum ScheduledVisitState {

	/**
	 * Objednána
	 */
	PLANNED("Objednán"),

	/**
	 * Zmeškána - přeobjednat
	 */
	MISSED("Zmeškáno"),

	/**
	 * Plánovaná k objednání
	 */
	TO_BE_ORGANIZED("K objednání");

	private String localized;

	private ScheduledVisitState(String localized) {
		this.localized = localized;
	}

	@Override
	public String toString() {
		return localized;
	}

}
