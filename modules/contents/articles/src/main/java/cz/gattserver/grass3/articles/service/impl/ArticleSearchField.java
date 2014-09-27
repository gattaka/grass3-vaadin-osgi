package cz.gattserver.grass3.articles.service.impl;

import cz.gattserver.grass3.search.service.ISearchField;

public enum ArticleSearchField implements ISearchField {

	NAME("Název"), CONTENT("Obsah");

	private String name;

	private ArticleSearchField(String name) {
		this.name = name;
	}

	public String getFieldName() {
		return name;
	}

}
