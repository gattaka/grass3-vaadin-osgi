package cz.gattserver.grass3.pg.events.impl;

import cz.gattserver.grass3.events.ProgressEvent;

public class PGProcessProgressEvent implements ProgressEvent {

	private String description;

	public PGProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String getStepDescription() {
		return description;
	}

}
