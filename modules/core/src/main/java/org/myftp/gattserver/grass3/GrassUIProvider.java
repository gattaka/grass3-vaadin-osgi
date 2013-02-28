package org.myftp.gattserver.grass3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

public class GrassUIProvider extends UIProvider {

	private static final long serialVersionUID = 4156516778293500959L;

	private static Logger logger = LoggerFactory
			.getLogger(GrassUIProvider.class);

	private final String beanName;

	public GrassUIProvider(String vaadinUIBeanName) {
		this.beanName = vaadinUIBeanName;
	}

	@Override
	public UI createInstance(UICreateEvent event) {
		logger.info("Creating new instance (by Spring) of '" + beanName + "'");
		return (UI) SpringApplicationContext.getApplicationContext().getBean(
				beanName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		logger.info("Returning class (by Spring) of '" + beanName + "'");
		return (Class<? extends UI>) SpringApplicationContext
				.getApplicationContext().getType(beanName);
	}
}
