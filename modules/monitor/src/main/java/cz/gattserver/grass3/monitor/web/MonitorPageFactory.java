package cz.gattserver.grass3.monitor.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("monitorPageFactory")
public class MonitorPageFactory extends AbstractPageFactory {

	public MonitorPageFactory() {
		super("system-monitor");
	}
}
