package cz.gattserver.grass3.events;

public interface IEventBus {

	public void publish(IEvent event);

	public void subscribe(Object listener);

	public void unsubscribe(Object listener);

}
