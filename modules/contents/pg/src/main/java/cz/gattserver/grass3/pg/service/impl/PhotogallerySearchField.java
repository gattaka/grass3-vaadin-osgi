package cz.gattserver.grass3.pg.service.impl;

import cz.gattserver.grass3.search.service.SearchField;

public enum PhotogallerySearchField implements SearchField {

	NAME("Název"), CONTENT("Obsah");

	private String name;

	private PhotogallerySearchField(String name) {
		this.name = name;
	}

	public String getFieldName() {
		return name;
	}

}
