package cz.gattserver.grass3.articles.service.impl;

import cz.gattserver.grass3.search.service.SearchField;

public enum ArticleSearchField implements SearchField {

	NAME("Název"), CONTENT("Obsah");

	private String name;

	private ArticleSearchField(String name) {
		this.name = name;
	}

	public String getFieldName() {
		return name;
	}

}
