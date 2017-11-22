package cz.gattserver.grass3.services;

public interface RandomSourceService {

	/**
	 * Získá náhodné celé číslo z rozsahu <0-range)
	 * 
	 * @param range
	 *            rozsah, ze kterého se bude vybírat
	 * @return náhodné číslo
	 */
	long getRandomNumber(long range);
}
