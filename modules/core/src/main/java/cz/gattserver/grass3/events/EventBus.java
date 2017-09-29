package cz.gattserver.grass3.events;

public interface EventBus {

	public void publish(Event event);

	public void subscribe(Object listener);

	public void unsubscribe(Object listener);

}
