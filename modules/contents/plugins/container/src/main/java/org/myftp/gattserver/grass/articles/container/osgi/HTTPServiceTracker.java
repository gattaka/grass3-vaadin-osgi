package org.myftp.gattserver.grass.articles.container.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

public class HTTPServiceTracker extends ServiceTracker {

	private HttpService httpService;
	public static String alias = "/org.myftp.gattserver.grass/org.myftp.gattserver.grass.articles.container/0.1";
	public static String path = "container";

	HTTPServiceTracker(BundleContext context) {
		super(context, HttpService.class.getName(), null);
	}

	@Override
	public final Object addingService(ServiceReference serviceReference) {
		httpService = (HttpService) super.addingService(serviceReference);
		try {
			httpService.registerResources(alias, path, null);
		} catch (NamespaceException e) {
			throw new IllegalArgumentException("Unable to mount [" + path + "] with alias '" + alias + "'.");
		}

		return httpService;
	}

	@Override
	public final void removedService(ServiceReference serviceReference, Object httpService) {
		this.httpService.unregister(alias);
		super.removedService(serviceReference, httpService);
	}

}
