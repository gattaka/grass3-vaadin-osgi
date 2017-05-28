package cz.gattserver.grass3.articles.events;

import cz.gattserver.grass3.events.IProgressEvent;

public class ArticlesProcessProgressEvent implements IProgressEvent {

	private static final long serialVersionUID = -6172189863516426907L;

	private String description;

	public ArticlesProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String getStepDescription() {
		return description;
	}

}
