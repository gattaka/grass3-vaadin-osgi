package cz.gattserver.grass3.pg.events.impl;

import cz.gattserver.grass3.events.StartEvent;

public class PGZipProcessStartEvent implements StartEvent {

	private int steps;

	public PGZipProcessStartEvent(int steps) {
		this.steps = steps;
	}

	@Override
	public int getCountOfStepsToDo() {
		return steps;
	}

}
