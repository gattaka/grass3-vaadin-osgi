package org.myftp.gattserver.grass3;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.myftp.gattserver.grass3.facades.SecurityFacade;
import org.myftp.gattserver.grass3.model.dto.UserDTO;
import org.myftp.gattserver.grass3.windows.HomeWindow;
import org.myftp.gattserver.grass3.windows.LoginWindow;
import org.myftp.gattserver.grass3.windows.QuotesWindow;
import org.myftp.gattserver.grass3.windows.SectionWindow;
import org.myftp.gattserver.grass3.windows.template.GrassWindow;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Window;

/**
 * Každá instance odpovídá jedné session, proto je potřeba hlídat instance ručně
 * a nesvěřit její vytváření Blueprintu, který by mohl instance sice vytvářet
 * sám, ale nevhodně
 */
@SuppressWarnings("serial")
public class GrassApplication extends Application implements
		HttpServletRequestListener {

	/**
	 * ThreadLocal pattern TODO v OSGi nějak nefunguje
	 */
	// private static ThreadLocal<GrassApplication> threadLocal = new
	// ThreadLocal<GrassApplication>();

	/**
	 * Instance hlavního okna
	 */
	private Window mainWindow;

	/**
	 * Úložiště auth údajů pro aktuální session
	 */
	private SecurityStore securityStore = new SecurityStore();

	/**
	 * Logger TODO
	 */
	// private final Logger logger =
	// LoggerFactory.getLogger(MainApplication.class);

	/**
	 * Fasády
	 */
	private SecurityFacade securityFacade = SecurityFacade.getInstance();

	/**
	 * Nahraje do aplikace všechny chráněné zdroje jako stránky apod., které
	 * jinak podléhají přihlášení
	 */
	private void loadProtectedResources() {
	}

	/**
	 * Authentikační metoda pro aplikaci
	 * 
	 * @param username
	 *            jméno uživatele, který se přihlašuje
	 * @param password
	 *            heslo, které použil
	 * @return true pokud se přihlášení zdařilo, jinak false
	 */
	public boolean authenticate(String username, String password) {

		UserDTO loggedUser = securityFacade.authenticate(username, password);
		if (loggedUser != null) {
			securityStore.setLoggedUser(loggedUser);
			loadProtectedResources();
			return true;
		}

		return false;
	}

	/**
	 * Získá aktuální instanci {@link SecurityStore} objektu, který je aplikací
	 * využíván
	 * 
	 * @return instance {@link SecurityStore}
	 */
	public SecurityStore getSecurityStore() {
		return securityStore;
	}

	/**
	 * Registr oken
	 */
	private Map<String, GrassWindow> windows = new HashMap<String, GrassWindow>();

	/**
	 * Registruje u {@link ServiceHolder} listenery pro bind a unbind sekcí.
	 * Stará se tak o přidání jejich instancí oken (od sekcí) do aplikace
	 */
	private void registerSectionBindListener() {
		ServiceHolder.getInstance().registerBindListener(ISection.class,
				new BindListener<ISection>() {

					public void onBind(ISection service) {
						GrassWindow window = service
								.getSectionWindowNewInstance();
						// v případě duplicity se nesmí záznam přepsat, protože
						// by se pak okno nedalo odebrat
						if (!windows.containsKey(service.getSectionName())) {
							windows.put(service.getSectionName(), window);
							addWindow(window);
						}
					}

					public void onUnbind(ISection service) {
						GrassWindow window = windows.get(service
								.getSectionName());
						if (window != null) {
							removeWindow(window);
							windows.remove(service.getSectionName());
						}
					}

				});
	}

	/**
	 * Default constructor
	 */
	public GrassApplication() {
		registerSectionBindListener();
	}

	@Override
	public void init() {

		// setInstance(this);

		// instance oken
		mainWindow = new HomeWindow();
		setMainWindow(mainWindow);

		addWindow(new LoginWindow());
		addWindow(new SectionWindow());
		addWindow(new QuotesWindow());
		
		// theme
		setTheme("grass");

	}

	// // @return the current application instance
	// public static GrassApplication getInstance() {
	// return threadLocal.get();
	// }
	//
	// // Set the current application instance
	// public static void setInstance(GrassApplication application) {
	// threadLocal.set(application);
	// }
	//
	public void onRequestStart(HttpServletRequest request,
			HttpServletResponse response) {

		// GrassApplication.setInstance(this);
	}

	public void onRequestEnd(HttpServletRequest request,
			HttpServletResponse response) {
		// threadLocal.remove();
	}

}
