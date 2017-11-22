package cz.gattserver.grass3.events.impl;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.MessagePublication;
import net.engio.mbassy.bus.config.BusConfiguration;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.events.Event;
import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.events.ProgressEvent;

import javax.annotation.PostConstruct;

@Component
public class EventBusImpl implements EventBus {

	private MBassador<Event> eventBus;

	@PostConstruct
	public void init() {
		eventBus = new MBassador<Event>(BusConfiguration.Default(4, 4, 8));
	}

	@Override
	public MessagePublication publish(ProgressEvent event) {
		return eventBus.publishAsync(event);
	}

	@Override
	public void publish(Event event) {
		eventBus.publish(event);
	}

	@Override
	public void subscribe(Object listener) {
		eventBus.subscribe(listener);
	}

	@Override
	public void unsubscribe(Object listener) {
		eventBus.unsubscribe(listener);
	}
}
