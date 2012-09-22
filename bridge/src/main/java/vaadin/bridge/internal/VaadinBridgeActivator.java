package vaadin.bridge.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * OSGi aktivátor
 */
public class VaadinBridgeActivator implements BundleActivator {

	/**
	 * Při startu tohoto bundlu se vytvoří rovnou HttpServiceTracker, který se
	 * postará o připojení všeho potřebného od Vaadinu, aby mohl jeho servlet
	 * běžet v OSGi prostředí
	 */
	public void start(BundleContext context) throws Exception {
		new HttpServiceTracker(context).open();
	}

	public void stop(BundleContext context) throws Exception {
	}
}