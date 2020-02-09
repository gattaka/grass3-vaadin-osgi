package cz.gattserver.grass3.articles.events.impl;

import cz.gattserver.grass3.events.ProgressEvent;

public class ArticlesProcessProgressEvent implements ProgressEvent {

	private String description;

	public ArticlesProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String getStepDescription() {
		return description;
	}

}
