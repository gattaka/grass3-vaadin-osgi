package org.myftp.gattserver.grass3.search.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchEntity {

	public static class Field {

		Enum<?> name;
		String content;
		boolean tokenized;

		public Field(Enum<?> name, String content, boolean tokenized) {
			this.name = name;
			this.content = content;
			this.tokenized = tokenized;
		}

	}

	/**
	 * Pole vyhledávané entity
	 */
	private List<Field> fields = new ArrayList<SearchEntity.Field>();

	/**
	 * Adresa (odkaz) na entitu v případě, že se objeví v přehledu výsledků
	 */
	private String url;

	public SearchEntity(String url) {
		this.url = url;
	}

	/**
	 * Immutable - vrací novou instanci interního listu
	 * 
	 * @see Collections.unmodifiableList
	 */
	public List<Field> getFields() {
		return Collections.unmodifiableList(fields);
	}

	public String getUrl() {
		return url;
	}

	public boolean addField(Field field) {
		return fields.add(field);
	}

	public boolean addField(Enum<?> name, String content, boolean tokenized) {
		return fields.add(new Field(name, content, tokenized));
	}
}
