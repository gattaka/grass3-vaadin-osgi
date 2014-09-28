package cz.gattserver.grass3.pg.events;

import cz.gattserver.grass3.events.IProgressEvent;

public class PGProcessProgressEvent implements IProgressEvent {

	private static final long serialVersionUID = -6172189863516426907L;

	private String description;

	public PGProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String getStepDescription() {
		return description;
	}

}
