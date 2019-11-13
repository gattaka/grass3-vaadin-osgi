package cz.gattserver.grass3.print3d.events.impl;

import cz.gattserver.grass3.events.StartEvent;

public class Print3dZipProcessStartEvent implements StartEvent {

	private int steps;

	public Print3dZipProcessStartEvent(int steps) {
		this.steps = steps;
	}

	@Override
	public int getCountOfStepsToDo() {
		return steps;
	}

}
