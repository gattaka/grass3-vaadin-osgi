package cz.gattserver.grass3.events;

import net.engio.mbassy.bus.MessagePublication;

public interface EventBus {

	/**
	 * Asynchronní publikace události průběhu operace
	 * 
	 * @param event
	 *            událost
	 * @return info objekt o asynchronním zveřejnění události
	 */
	public MessagePublication publish(ProgressEvent event);

	/**
	 * Synchronní publikace události
	 * 
	 * @param event
	 *            událost
	 */
	public void publish(Event event);

	public void subscribe(Object listener);

	public void unsubscribe(Object listener);

}
