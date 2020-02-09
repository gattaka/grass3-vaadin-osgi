package cz.gattserver.grass3.events;

import cz.gattserver.grass3.events.StartEvent;

public class MockProcessStartEvent implements StartEvent {

	private int steps;

	public MockProcessStartEvent(int steps) {
		this.steps = steps;
	}

	@Override
	public int getCountOfStepsToDo() {
		return steps;
	}

}
