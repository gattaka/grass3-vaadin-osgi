package org.myftp.gattserver.grass3;

import java.util.HashMap;
import java.util.Map;

import org.myftp.gattserver.grass3.facades.SecurityFacade;
import org.myftp.gattserver.grass3.model.dto.UserDTO;
import org.myftp.gattserver.grass3.security.SecurityStore;
import org.myftp.gattserver.grass3.windows.HomeWindow;
import org.myftp.gattserver.grass3.windows.LoginWindow;
import org.myftp.gattserver.grass3.windows.QuotesWindow;
import org.myftp.gattserver.grass3.windows.SectionWindow;
import org.myftp.gattserver.grass3.windows.template.GrassWindow;

import com.vaadin.Application;
import com.vaadin.ui.Window;

/**
 * Každá instance odpovídá jedné session, proto je potřeba hlídat instance ručně
 * a nesvěřit její vytváření Blueprintu, který by mohl instance sice vytvářet
 * sám, ale nevhodně
 */
@SuppressWarnings("serial")
public class GrassApplication extends Application implements
		BindListener<ISection> {

	/**
	 * Instance hlavního okna
	 */
	private Window mainWindow;

	/**
	 * Úložiště auth údajů pro aktuální session
	 */
	private SecurityStore securityStore = new SecurityStore();

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
			// TODO
			// securityStore.setLoggedUser(loggedUser);
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
		ServiceHolder.getInstance().registerBindListener(ISection.class, this);
	}

	@Override
	public void init() {

		// Nelze volat z konstruktoru, musí se později (při přikládání nových
		// addWindow musí existovat instance app)
		registerSectionBindListener();

		// instance oken
		mainWindow = new HomeWindow();
		setMainWindow(mainWindow);

		addWindow(new LoginWindow());
		addWindow(new SectionWindow());
		addWindow(new QuotesWindow());

		// theme
		setTheme("grass");

	}

	public void onBind(ISection service) {
		GrassWindow window = service.getSectionWindowNewInstance();
		// v případě duplicity se nesmí záznam přepsat, protože
		// by se pak okno nedalo odebrat
		if (!windows.containsKey(service.getSectionIDName())) {
			windows.put(service.getSectionIDName(), window);
			addWindow(window);
		}
	}

	public void onUnbind(ISection service) {
		GrassWindow window = windows.get(service.getSectionIDName());
		if (window != null) {
			removeWindow(window);
			windows.remove(service.getSectionIDName());
		}
	}

}
