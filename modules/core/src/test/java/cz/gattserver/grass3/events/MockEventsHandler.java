package cz.gattserver.grass3.events;

import java.util.concurrent.CompletableFuture;

import net.engio.mbassy.listener.Handler;

public class MockEventsHandler {

	public int state = 0;
	public int steps = 0;
	public int currentStep = 0;
	public String currentStepDesc = null;
	
	public CompletableFuture<MockEventsHandler> future;

	public CompletableFuture<MockEventsHandler> expectEvent() {
		future = new CompletableFuture<>();
		return future;
	}
	
	@Handler
	public void onStart(MockProcessStartEvent event) {
		state = 1;
		steps = event.getCountOfStepsToDo();
	}

	@Handler
	public void onProgress(MockProcessProgressEvent event) {
		state = 2;
		currentStep++;
		currentStepDesc = event.getStepDescription();
		future.complete(this);
	}

	@Handler
	public void onResult(MockProcessResultEvent event) {
		state = 3;
	}

}
