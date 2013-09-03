package org.myftp.gattserver.grass3.pg.service.impl;

import org.myftp.gattserver.grass3.search.service.ISearchField;

public enum PhotogallerySearchField implements ISearchField {

	NAME("Název"), CONTENT("Obsah");

	private String name;

	private PhotogallerySearchField(String name) {
		this.name = name;
	}

	public String getFieldName() {
		return name;
	}

}
