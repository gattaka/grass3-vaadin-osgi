package org.myftp.gattserver.grass3.bridge;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicReference;

import vaadin.bridge.ApplicationFactory;

import org.myftp.gattserver.grass3.GrassUI;
import org.osgi.service.log.LogService;

import com.vaadin.Application;
import com.vaadin.Application.SystemMessages;

/**
 * Vaadin Bridge service třída - registruje se pomocí Blueprint jako service,
 * odpovídající {@link ApplicationFactory} a zavádí tak Vaadin aplikaci (Servlet
 * apod.). Funguje jako factory, takže vytváří instanci aplikace.
 * 
 * @author gatt
 * 
 */
public class AppFactory implements ApplicationFactory {

	AtomicReference<LogService> logRef = new AtomicReference<LogService>(null);

	// @Reference(dynamic = true, optional = true)
	public void setLogService(LogService log) {
		logRef.set(log);
	}

	public void unsetLogService(LogService log) {
		logRef.compareAndSet(log, null);
	}

	public String getApplicationCSSClassName() {
		return GrassUI.class.getSimpleName();
	}

	public SystemMessages getSystemMessages() {
		return null;
	}

	/**
	 * Podstatné je, že tato metoda se volá pokaždé u nové session, takže není
	 * možné vytvořit jednu instanci aplikace - ostatně i samotný Vaadin FW
	 * takto funguje. Instance aplikace odpovídá jedné session.
	 */
	public Application newInstance() {
		return new GrassUI();
	}

	public Dictionary<String, String> getInitParams() {
		Dictionary<String, String> initParams = new Hashtable<String, String>();
		// Multiupload
		initParams.put("widgetset", "org.myftp.gattserver.grass3.gwt.UploadWidgetSet");
		return initParams;
	}

}
