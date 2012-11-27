package org.myftp.gattserver.grass3.util;

import org.myftp.gattserver.grass3.model.dto.NodeDTO;

public class CategoryUtils {

	/**
	 * <p>
	 * Vytvoří URL identifikátor kategorie ve tvaru
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
	 * @param category
	 *            {@link NodeDTO} objekt kategorie
	 * @return URL identifikátor kategorie
	 */
	public static String createURLIdentifier(NodeDTO category) {
		return String.valueOf(category.getId()) + "-" + category.getName();
	}

	/**
	 * Naparsuje URL identifikátor a vrátí jeho položky v novém {@link NodeDTO}
	 * objektu - <b>tento objekt tím pádem má naplněny pouze identifikační pole
	 * a nezaručuje existenci takového objektu v DB</b>
	 * 
	 * @param identifier
	 *            {@link String} identifikátor
	 * @return {@link NodeDTO} objekt s identifikačními údaji, nebo {@code null}
	 *         pokud nejsou splněny
	 */
	public static NodeDTO parseURLIdentifier(String identifier) {

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
		
		NodeDTO node = new NodeDTO();
		node.setId(id);
		node.setName(parts[1]);
		
		return node;

	}

}
