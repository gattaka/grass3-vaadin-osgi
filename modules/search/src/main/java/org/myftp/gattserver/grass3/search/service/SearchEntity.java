package org.myftp.gattserver.grass3.search.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;

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

		private IPageFactory viewerPageFactory;
		private String suffix;

		public Link(IPageFactory viewerPageFactory, String suffix) {
			this.viewerPageFactory = viewerPageFactory;
			this.suffix = suffix;
		}

		public IPageFactory getViewerPageFactory() {
			return viewerPageFactory;
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

	public SearchEntity(IPageFactory viewerPageFactory, String suffix) {
		Link link = new Link(viewerPageFactory, suffix);
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
