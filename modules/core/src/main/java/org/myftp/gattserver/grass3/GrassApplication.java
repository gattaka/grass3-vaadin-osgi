package org.myftp.gattserver.grass3;

import java.util.HashMap;
import java.util.Map;

import org.myftp.gattserver.grass3.model.domain.User;
import org.myftp.gattserver.grass3.security.SecurityFacade;
import org.myftp.gattserver.grass3.windows.HomeWindow;
import org.myftp.gattserver.grass3.windows.LoginWindow;
import org.myftp.gattserver.grass3.windows.QuotesWindow;
import org.myftp.gattserver.grass3.windows.RegistrationWindow;
import org.myftp.gattserver.grass3.windows.err.Err404;
import org.myftp.gattserver.grass3.windows.err.Err500;
import org.myftp.gattserver.grass3.windows.template.SettingsWindow;

import com.vaadin.Application;
import com.vaadin.ui.Window;

/**
 * Každá instance odpovídá jedné session, proto je potřeba hlídat instance ručně
 * a nesvěřit její vytváření Blueprintu, který by mohl instance sice vytvářet
 * sám, ale nevhodně
 */
@SuppressWarnings("serial")
public class GrassApplication extends Application {

	/**
	 * Instance hlavního okna
	 */
	private Window mainWindow;

	/**
	 * Fasády
	 */
	private SecurityFacade securityFacade = SecurityFacade.getInstance();

	/**
	 * Inicializováno ?
	 * 
	 * Tento flag udává, zda byla aplikace již inicializována. Inicializace
	 * končí poté, co jsou doregistrovány všechny windows. Je to důležité
	 * rozlišovat, protože regisrtace okna pouze prováže jeho instanci s jeho
	 * jménem (třídy) a aplikace (GrassApplication) tak ví o jeho přítomnosti v
	 * systému. Ještě ale není postaven jeho layout. Je to tak úmyslně protože
	 * okna při vystavování layoutu spoléhají na to, že jsou v aplikaci
	 * zaregistrovaná všechny ostatní povinná okna.
	 * 
	 * Teprve poté, co jsou okna zaregistrována, je možné je začít stavět. V
	 * případě interních oken (Home, Login atd.) není problém toto jednoduše
	 * rozdělit do dvou fází. V případě dynamicky registrovaných oken však není
	 * jasné zda se může volat registrace a rovnou addWindow (Vaadin app) nebo
	 * se addWindow musí počkat až si GrassApplication doregistruje povinná
	 * okna. Proto je potřeba mít proměnnou, která indikuje, že je aplikace
	 * připravena k přidávání oken pomocí addWindow a tedy jejich výstavbu
	 * layoutů.
	 */
	private Boolean initialized = false;

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

		User loggedUser = securityFacade.authenticate(username, password);
		if (loggedUser != null) {
			setUser(loggedUser);
			loadProtectedResources();
			return true;
		}

		return false;
	}

	private void loadProtectedResources() {
	}

	@Override
	public void init() {

		// registrace aplikace do ServiceHolderu
		ServiceHolder.getInstance().registerListenerApp(this);

		// theme
		setTheme("grass");

		mainWindow = new HomeWindow();
		setMainWindow(mainWindow);

		addWindow(new LoginWindow());
		addWindow(new QuotesWindow());
		addWindow(new SettingsWindow());
		addWindow(new RegistrationWindow());

		// err okna
		addWindow(new Err404());
		addWindow(new Err500());

		// Interní okna jsou registrována, může se začít stavět jejich layout
		synchronized (initialized) {
			initialized = true;
			for (Window window : windows.values())
				super.addWindow(window);
		}

	}

	/**
	 * Registr oken - je to takhle odvrtvené, protože Vaadin registruje okna dle
	 * jejich jména - to je ale většinou známo až z instance okna, já potřebuju
	 * vědět jak bude řazeno okno již z jeho třídy.
	 */
	private Map<Class<? extends Window>, Window> windows = new HashMap<Class<? extends Window>, Window>();

	/**
	 * Získá okno dle třídy
	 */
	public Window getWindow(Class<? extends Window> windowClass) {
		return windows.get(windowClass);
	}

	/**
	 * Získá okno dle jména
	 */
	public Window getWindow(String name) {
		return super.getWindow(name);
	}

	/**
	 * Zaregistruje okno do aplikace pod daným jménem
	 */
	public void addWindow(Window window) {
		System.out.println("addWindow by class: " + window.getClass());
		if (!windows.containsKey(window.getClass())) {
			windows.put(window.getClass(), window);
			synchronized (initialized) {
				if (initialized)
					super.addWindow(window);
			}
		}
	}

	/**
	 * Odregistruje okno dle třídy
	 */
	public void removeWindow(Class<? extends Window> windowClass) {
		removeWindow(windows.get(windowClass));
	}

	/**
	 * Odregistruje okno dle instance
	 */
	@Override
	public void removeWindow(Window window) {
		if (window != null) {
			windows.remove(window.getClass());
			synchronized (initialized) {
				if (initialized)
					super.removeWindow(window);
			}
		}
	}

}
