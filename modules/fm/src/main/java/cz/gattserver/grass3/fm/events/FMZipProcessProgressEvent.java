package cz.gattserver.grass3.fm.events;

import cz.gattserver.grass3.events.ProgressEvent;

public class FMZipProcessProgressEvent implements ProgressEvent {

	private String description;

	public FMZipProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String getStepDescription() {
		return description;
	}

}
