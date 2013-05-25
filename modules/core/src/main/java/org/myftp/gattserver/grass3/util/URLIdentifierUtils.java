package org.myftp.gattserver.grass3.util;

public class URLIdentifierUtils {

	public static class URLIdentifier {
		private String name;
		private Long id;

		private URLIdentifier(Long id, String name) {
			this.id = id;
			this.name = name;
		}

		public Long getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}

	/**
	 * <p>
	 * Vytvoří URL identifikátor ve tvaru
	 * </p>
	 * 
	 * <pre>
	 * ID - Název
	 * </pre>
	 * 
	 * <p>
	 * tedy například
	 * </p>
	 * 
	 * <pre>
	 * 21 - Software
	 * </pre>
	 * 
	 * @param id
	 *            číselný identifikátor
	 * @param name
	 *            jmenný identifikátor
	 * @return URL identifikátor kategorie
	 */
	public static String createURLIdentifier(Long id, String name) {
		return String.valueOf(id) + "-" + name;
	}

	/**
	 * Naparsuje URL identifikátor a vrátí jeho položky v novém
	 * {@link URLIdentifier} objektu
	 * 
	 * @param identifier
	 *            {@link String} identifikátor
	 * @return {@link URLIdentifier} objekt s identifikačními údaji, nebo
	 *         {@code null} pokud nejsou splněny
	 */
	public static URLIdentifier parseURLIdentifier(String identifier) {

		if (identifier == null)
			return null;

		// získej ID
		String[] parts = identifier.split("-");
		if (parts.length <= 1)
			return null;

		Long id = null;
		try {
			id = Long.valueOf(parts[0]);
		} catch (NumberFormatException e) {
			return null;
		}

		URLIdentifier urlIdentifier = new URLIdentifier(id, parts[1]);

		return urlIdentifier;

	}

}
