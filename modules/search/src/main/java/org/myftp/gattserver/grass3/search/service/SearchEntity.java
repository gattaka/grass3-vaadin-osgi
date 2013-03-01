package org.myftp.gattserver.grass3.search.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.myftp.gattserver.grass3.pages.template.GrassWindow;

public class SearchEntity {

	public static class Field {

		private Enum<? extends ISearchField> name;
		private String content;
		private boolean tokenized;

		public Field(Enum<? extends ISearchField> name, String content,
				boolean tokenized) {
			this.name = name;
			this.content = content;
			this.tokenized = tokenized;
		}

		public Enum<? extends ISearchField> getName() {
			return name;
		}

		public String getContent() {
			return content;
		}

		public boolean isTokenized() {
			return tokenized;
		}

	}

	public static class Link {

		private Class<? extends GrassWindow> viewerClass;
		private String suffix;

		public Link(Class<? extends GrassWindow> viewerClass, String suffix) {
			this.viewerClass = viewerClass;
			this.suffix = suffix;
		}

		public Class<? extends GrassWindow> getViewerClass() {
			return viewerClass;
		}

		public String getSuffix() {
			return suffix;
		}

	}

	/**
	 * Pole vyhledávané entity
	 */
	private List<Field> fields = new ArrayList<SearchEntity.Field>();

	/**
	 * Adresa (odkaz) na entitu v případě, že se objeví v přehledu výsledků
	 */
	private Link link;

	public SearchEntity(Class<? extends GrassWindow> viewerClass, String suffix) {
		Link link = new Link(viewerClass, suffix);
		this.link = link;
	}

	/**
	 * Immutable - vrací novou instanci interního listu
	 * 
	 * @see Collections.unmodifiableList
	 */
	public List<Field> getFields() {
		return Collections.unmodifiableList(fields);
	}

	public Link getLink() {
		return link;
	}

	public boolean addField(Enum<? extends ISearchField> name, String content,
			boolean tokenized) {
		return fields.add(new Field(name, content, tokenized));
	}
}
