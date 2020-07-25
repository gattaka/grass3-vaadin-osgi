package cz.gattserver.grass3.fm.events;

import cz.gattserver.grass3.events.StartEvent;

public class FMZipProcessStartEvent implements StartEvent {

	private int steps;

	public FMZipProcessStartEvent(int steps) {
		this.steps = steps;
	}

	@Override
	public int getCountOfStepsToDo() {
		return steps;
	}

}
