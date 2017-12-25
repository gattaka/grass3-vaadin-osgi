package cz.gattserver.grass3.pg.events;

import cz.gattserver.grass3.events.StartEvent;

public class PGProcessStartEvent implements StartEvent {

	private int steps;

	public PGProcessStartEvent(int steps) {
		this.steps = steps;
	}

	@Override
	public int getCountOfStepsToDo() {
		return steps;
	}

}
