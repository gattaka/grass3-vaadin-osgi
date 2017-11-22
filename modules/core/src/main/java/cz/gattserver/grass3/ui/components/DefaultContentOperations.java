package cz.gattserver.grass3.ui.components;

/**
 * Nejběžnější operace s obsahy
 * 
 * @author gatt
 * 
 */
public enum DefaultContentOperations {

	
	/**
	 * Vytvoření
	 */
	NEW,
	
	/**
	 * Úprava
	 */
	EDIT,
	
	/**
	 * Smazání
	 */
	DELETE;

	public String toString() {
		return super.toString().toLowerCase();
	};
	
}
