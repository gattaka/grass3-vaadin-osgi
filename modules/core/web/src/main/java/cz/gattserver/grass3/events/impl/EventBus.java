package cz.gattserver.grass3.events.impl;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.events.IEvent;
import cz.gattserver.grass3.events.IEventBus;
import cz.gattserver.grass3.events.IProgressEvent;

import javax.annotation.PostConstruct;

@Component
public class EventBus implements IEventBus {

	private MBassador<IEvent> eventBus;

	@PostConstruct
	public void init() {
		eventBus = new MBassador<IEvent>(BusConfiguration.Default(4, 4, 8));
	}

	@Override
	public void publish(IEvent event) {
		if (event instanceof IProgressEvent) {
			eventBus.publishAsync(event);
		} else {
			eventBus.publish(event);
		}
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
