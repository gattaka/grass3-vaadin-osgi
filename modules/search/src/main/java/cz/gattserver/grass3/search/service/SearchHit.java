package cz.gattserver.grass3.search.service;

public class SearchHit {

	/**
	 * Obsah pole ve kterém byl nalezen hledaný text
	 */
	private String hitFieldText;

	/**
	 * Název pole ve kterém byl nalezen hledaný text
	 */
	private String hitFieldName;

	/**
	 * Odkaz na obsah s polem, ve kterém byl nalezen hledaný text
	 */
	private String contentLink;

	public SearchHit(String hitFieldText, String hitFieldName,
			String contentLink) {
		this.hitFieldText = hitFieldText;
		this.hitFieldName = hitFieldName;
		this.contentLink = contentLink;
	}

	public String getHitFieldName() {
		return hitFieldName;
	}

	public String getHitFieldText() {
		return hitFieldText;
	}

	public String getContentLink() {
		return contentLink;
	}

}
