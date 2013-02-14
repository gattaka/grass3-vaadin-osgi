package org.vaadin7.osgi.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentFactory;
import org.vaadin7.osgi.Constants;
import org.vaadin7.osgi.OSGiUIProvider;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinServletSession;
import com.vaadin.ui.UI;

public class OSGiVaadinSession extends VaadinServletSession {

	private Map<ServiceReference, OSGiUIProvider> uiProviders = new HashMap<ServiceReference, OSGiUIProvider>();
	private BundleContext context;

	private List<ServiceReference> pendingFactories = Collections
			.synchronizedList(new ArrayList<ServiceReference>());

	public OSGiVaadinSession() {
		System.out.println("created");
	}

	@Activate
	public void activate(BundleContext context) throws InvalidSyntaxException {
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Deprecated
	public void start(SessionStartEvent event) {
		super.start(event);

		VaadinService service = VaadinServletService.getCurrent();
		if (!hasVaadinServiceData(service)) {
			addVaadinServiceData(new VaadinService.VaadinServiceData(service));
		}

		for (ServiceReference reference : pendingFactories) {
			ComponentFactory factory = (ComponentFactory) context
					.getService(reference);
			String name = (String) reference.getProperty("component.factory");
			String className = name.substring(Constants.PREFIX__UI_CLASS
					.length());
			OSGiUIProvider uiProvider = null;
			try {
				uiProvider = new OSGiUIProvider(factory,
						(Class<? extends UI>) context.getBundle().loadClass(
								className), null);
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
			if (uiProvider != null) {
				uiProviders.put(reference, uiProvider);
				service.addUIProvider(this, uiProvider);
			}
		}
		pendingFactories.clear();
	}

	@Deactivate
	public void deactivate() {
		context = null;
	}

	/**
	 * Called by OSGi-DS.
	 * 
	 * @param reference
	 */
	// TODO could be addUI(ComponentFactory, Map), but could not add
	// <scr:component xmlns:scr="http://www.osgi.org/xmlns/scr/v1.1.0" to
	// component definition using bndTools
	@Reference(unbind = "removeUI", dynamic = true, optional = true, multiple = true, target = "component.factory=org.vaadin.UI/*")
	public void addUI(ServiceReference reference) {
		pendingFactories.add(reference);
	}

	/**
	 * Called by OSGi-DS.
	 * 
	 * @param reference
	 */
	// TODO could be removeUI(ComponentFactory, Map), but could not add
	// <scr:component xmlns:scr="http://www.osgi.org/xmlns/scr/v1.1.0" to
	// component definition using bndTools
	public void removeUI(ServiceReference reference) {
		VaadinService service = VaadinServletService.getCurrent();
		OSGiUIProvider uiProvider = uiProviders.remove(reference);
		if (uiProvider != null) {
			service.removeUIProvider(this, uiProvider);
		}
	}
}
