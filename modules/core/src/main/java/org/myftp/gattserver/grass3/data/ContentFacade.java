package org.myftp.gattserver.grass3.data;

import java.util.ArrayList;
import java.util.List;

public class ContentFacade {

	private static ContentFacade instance;
	private List<Content> contents = new ArrayList<ContentFacade.Content>();

	public static class Content {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public Content(String name) {
			Content.this.name = name;
		}
	}

	private ContentFacade() {
		contents.add(new Content("Content #1"));
		contents.add(new Content("Content #2"));
		contents.add(new Content("Content #3"));
	}

	public static ContentFacade getInstance() {
		if (instance == null)
			instance = new ContentFacade();
		return instance;
	}

	public List<Content> getContents() {
		return contents;
	}

}
