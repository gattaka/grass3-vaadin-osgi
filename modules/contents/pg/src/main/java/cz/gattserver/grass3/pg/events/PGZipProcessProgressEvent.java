package cz.gattserver.grass3.pg.events;

import cz.gattserver.grass3.events.ProgressEvent;

public class PGZipProcessProgressEvent implements ProgressEvent {

	private static final long serialVersionUID = -6172189863516426907L;

	private String description;

	public PGZipProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String getStepDescription() {
		return description;
	}

}
