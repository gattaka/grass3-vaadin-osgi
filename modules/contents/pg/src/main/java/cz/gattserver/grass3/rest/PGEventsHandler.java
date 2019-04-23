package cz.gattserver.grass3.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import cz.gattserver.grass3.pg.events.impl.PGProcessResultEvent;
import net.engio.mbassy.listener.Handler;

public class PGEventsHandler {

	private static Map<UUID, CompletableFuture<PGEventsHandler>> futureMap = new HashMap<>();
	private static Map<UUID, PGProcessResultEvent> resultsMap = new HashMap<>();

	public CompletableFuture<PGEventsHandler> expectEvent(UUID uuid) {
		CompletableFuture<PGEventsHandler> future = new CompletableFuture<>();
		synchronized (futureMap) {
			futureMap.put(uuid, future);
		}
		return future;
	}

	public PGProcessResultEvent getResultAndDelete(UUID uuid) {
		synchronized (futureMap) {
			PGProcessResultEvent result = resultsMap.get(uuid);
			resultsMap.remove(uuid);
			return result;
		}
	}

	@Handler
	public void onResult(PGProcessResultEvent event) {
		synchronized (futureMap) {
			CompletableFuture<PGEventsHandler> future = futureMap.get(event.getOperationId());
			if (future != null) {
				resultsMap.put(event.getOperationId(), event);
				future.complete(this);
			}
		}
	}

}
