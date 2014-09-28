package cz.gattserver.grass3.pg.events;

import cz.gattserver.grass3.events.IStartEvent;

public class PGProcessStartEvent implements IStartEvent {
	
	private static final long serialVersionUID = -6327153009560081031L;

	private int steps;

	public PGProcessStartEvent(int steps) {
		this.steps = steps;
	}

	@Override
	public int getCountOfStepsToDo() {
		return steps;
	}

}
