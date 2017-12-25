package cz.gattserver.grass3.pg.search;

import cz.gattserver.grass3.search.service.SearchField;

public enum PGSearchField implements SearchField {

	NAME("Název"), CONTENT("Obsah");

	private String name;

	private PGSearchField(String name) {
		this.name = name;
	}

	public String getFieldName() {
		return name;
	}

}
