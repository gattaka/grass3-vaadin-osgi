package org.myftp.gattserver.grass.articles.container.osgi;

import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass.articles.container.plugin.ContainerFactory;
import org.myftp.gattserver.grass.articles.parser.interfaces.IPluginFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;



public class Activator implements BundleActivator {

	private List<ServiceRegistration> registrations = new ArrayList<ServiceRegistration>();
	private ServiceTracker httpTracker;

	public void start(BundleContext context) throws Exception {

		/**
		 * Resources
		 */
		httpTracker = new HTTPServiceTracker(context);
		httpTracker.open();

		/**
		 * Zaregistruj poskytované pluginy
		 */
		registrations.add(context.registerService(IPluginFactory.class.getName(), new ContainerFactory(), null));

	}

	public void stop(BundleContext context) throws Exception {
		for (ServiceRegistration registration : registrations) {
			registration.unregister();
		}
		registrations = null;
		httpTracker.close();
		httpTracker = null;
	}

}
