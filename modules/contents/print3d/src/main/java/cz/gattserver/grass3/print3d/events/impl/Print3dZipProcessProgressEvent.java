package cz.gattserver.grass3.print3d.events.impl;

import cz.gattserver.grass3.events.ProgressEvent;

public class Print3dZipProcessProgressEvent implements ProgressEvent {

	private String description;

	public Print3dZipProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String getStepDescription() {
		return description;
	}

}
