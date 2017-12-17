package cz.gattserver.grass3.events;

import cz.gattserver.grass3.events.ProgressEvent;

public class MockProcessProgressEvent implements ProgressEvent {

	private String description;

	public MockProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String getStepDescription() {
		return description;
	}

}
