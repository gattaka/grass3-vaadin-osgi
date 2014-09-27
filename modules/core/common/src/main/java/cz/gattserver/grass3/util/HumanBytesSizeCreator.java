package cz.gattserver.grass3.util;

public class HumanBytesSizeCreator {

	/**
	 * Vytvoří popis velikosti souboru v lidsky čitelných hodnotách (kB, MB ...)
	 * 
	 * @param bytes
	 *            velikost jež se zpracovává
	 * @param si
	 *            mají být velikosti počítány jako binární ? (si = true) kB ~
	 *            1000, kiB ~ 1024 (si = false)
	 * @return řetězec s popisem velikosti souboru
	 */
	// http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	public static String format(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
				+ (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
