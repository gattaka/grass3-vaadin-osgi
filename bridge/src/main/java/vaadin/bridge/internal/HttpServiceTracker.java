package vaadin.bridge.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

class HttpServiceTracker extends ServiceTracker {

	/**
	 * Protože je poslední parametr {@link ServiceTrackerCustomizer} null, bude
	 * volána metoda addingService, ve které bude proveden zbytek úkonů.
	 */
	HttpServiceTracker(BundleContext context) {
		super(context, HttpService.class.getName(), null);
	}

	@Override
	public Object addingService(ServiceReference reference) {

		// Získej instanci Http služby OSGi kontejneru
		HttpService httpService = (HttpService) context.getService(reference);

		// Zaregistruj Vaadin resources (CSS, JS, themes obecně ...) -
		// registruje je na URI /VAADIN a registruje resources z adresáře
		// /VAADIN
		try {
			httpService.registerResources("/VAADIN", "/VAADIN",
					new TargetBundleHttpContext(context, "com.vaadin"));
		} catch (NamespaceException e) {
			e.printStackTrace();
		}

		// Vytvoř tracker pro aplikace (resp. jejich factories), které jsou
		// napsané pro Vaadin
		ApplicationFactoryTracker bridge = new ApplicationFactoryTracker(
				httpService, context);
		bridge.open();

		return bridge;
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		ApplicationFactoryTracker bridge = (ApplicationFactoryTracker) service;
		bridge.close();
		context.ungetService(reference);
	}
}
