package org.vaadin7.osgi;

import java.util.Dictionary;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

public class OSGiUIProvider extends UIProvider {

	private final ComponentFactory factory;
	private final Class<? extends UI> uiClass;
	private ComponentInstance instance;

	@SuppressWarnings("rawtypes")
	public OSGiUIProvider(ComponentFactory factory,
			Class<? extends UI> uiClass, Dictionary properties) {
		super();
		this.factory = factory;
		this.uiClass = uiClass;
	}

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return uiClass;
	}

	@Override
	public UI createInstance(UICreateEvent event) {
		instance = factory.newInstance(null);
		return (UI) instance.getInstance();
	}

	public void dispose() {
		if (instance != null) {
			instance.dispose();
			instance = null;
		}
	}

}
