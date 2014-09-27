package cz.gattserver.grass3.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

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
		try {
			String identifier = URLEncoder.encode(String.valueOf(id) + "-" + name, "UTF-8");
			// Tomcat má dfault nastavené ignorovat adresy ve kterých je %2F
			// https://www.assembla.com/spaces/liftweb/wiki/Tomcat/print
			// http://forum.spring.io/forum/spring-projects/web/97212-url-encoded-in-pathvariable-value-causes-problems
			return identifier.replaceAll("%2F","%252F");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 missing - vážně ?
			e.printStackTrace();
			return null;
		}
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

		try {
			String name = URLDecoder.decode(parts[1], "UTF-8");
			URLIdentifier urlIdentifier = new URLIdentifier(id, name);
			return urlIdentifier;
		} catch (UnsupportedEncodingException e) {
			// UTF-8 missing - vážně ?
			e.printStackTrace();
			return null;
		}
	}
}
