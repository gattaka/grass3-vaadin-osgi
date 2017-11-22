package cz.gattserver.grass3.events;

import cz.gattserver.grass3.events.ProgressEvent;

public class MockProcessProgressEvent implements ProgressEvent {

	private static final long serialVersionUID = -6172189863516426907L;

	private String description;

	public MockProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String getStepDescription() {
		return description;
	}

}
