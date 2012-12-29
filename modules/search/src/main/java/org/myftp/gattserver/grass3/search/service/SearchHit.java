package org.myftp.gattserver.grass3.search.service;

public class SearchHit { 

	/**
	 * Obsah pole ve kterém byl nalezen hledaný text
	 */
	private String hitFieldText;

	/**
	 * Odkaz na obsah s polem, ve kterém byl nalezen hledaný text
	 */
	private String contentLink;

	public SearchHit(String hitFieldText, String contentLink) {
		this.hitFieldText = hitFieldText;
		this.contentLink = contentLink;
	}
 
	public String getHitFieldText() {
		return hitFieldText;
	}

	public String getContentLink() {
		return contentLink;
	}

}
