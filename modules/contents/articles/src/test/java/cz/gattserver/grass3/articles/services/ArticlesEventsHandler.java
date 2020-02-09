package cz.gattserver.grass3.articles.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import cz.gattserver.grass3.articles.events.impl.ArticlesProcessResultEvent;
import net.engio.mbassy.listener.Handler;

public class ArticlesEventsHandler {

	private static Map<UUID, CompletableFuture<ArticlesEventsHandler>> futureMap = new HashMap<>();
	private static Map<UUID, ArticlesProcessResultEvent> resultsMap = new HashMap<>();

	public CompletableFuture<ArticlesEventsHandler> expectEvent(UUID uuid) {
		CompletableFuture<ArticlesEventsHandler> future = new CompletableFuture<>();
		synchronized (futureMap) {
			futureMap.put(uuid, future);
		}
		return future;
	}

	public ArticlesProcessResultEvent getResultAndDelete(UUID uuid) {
		synchronized (futureMap) {
			ArticlesProcessResultEvent result = resultsMap.get(uuid);
			resultsMap.remove(uuid);
			return result;
		}
	}

	@Handler
	public void onResult(ArticlesProcessResultEvent event) {
		synchronized (futureMap) {
			CompletableFuture<ArticlesEventsHandler> future = futureMap.get(event.getOperationId());
			if (future != null) {
				resultsMap.put(event.getOperationId(), event);
				future.complete(this);
			}
		}
	}

}
