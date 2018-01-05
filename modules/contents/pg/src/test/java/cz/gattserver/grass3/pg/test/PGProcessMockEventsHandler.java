package cz.gattserver.grass3.pg.test;

import java.util.concurrent.CompletableFuture;

import cz.gattserver.grass3.pg.events.impl.PGProcessResultEvent;
import net.engio.mbassy.listener.Handler;

public class PGProcessMockEventsHandler {

	public volatile Long pgId = 0L;
	public volatile String resultDetails = null;
	public volatile boolean success = false;

	public CompletableFuture<PGProcessMockEventsHandler> future;

	public CompletableFuture<PGProcessMockEventsHandler> expectEvent() {
		future = new CompletableFuture<>();
		return future;
	}

	@Handler
	public void onResult(PGProcessResultEvent event) {
		pgId = event.getGalleryId();
		resultDetails = event.getResultDetails();
		success = event.isSuccess();
		future.complete(this);
	}

}
