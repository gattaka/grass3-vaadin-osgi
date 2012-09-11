package org.myftp.gattserver.grass3;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.myftp.gattserver.grass3.facades.SecurityFacade;
import org.myftp.gattserver.grass3.model.dto.UserDTO;
import org.myftp.gattserver.grass3.windows.HomeWindow;
import org.myftp.gattserver.grass3.windows.LoginWindow;
import org.myftp.gattserver.grass3.windows.QuotesWindow;
import org.myftp.gattserver.grass3.windows.SectionWindow;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class GrassApplication extends Application implements
		HttpServletRequestListener {

	private static ThreadLocal<GrassApplication> threadLocal = new ThreadLocal<GrassApplication>();

	private Window mainWindow;
	private SecurityStore securityStore = new SecurityStore();

	// Fasády
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

	@Override
	public void init() {

		setInstance(this);

		// init okna
		mainWindow = new HomeWindow();
		setMainWindow(mainWindow);

		addWindow(new LoginWindow());
		addWindow(new SectionWindow());
		addWindow(new QuotesWindow());

		// theme
		setTheme("grass");

	}

	// @return the current application instance
	public static GrassApplication getInstance() {
		return threadLocal.get();
	}

	// Set the current application instance
	public static void setInstance(GrassApplication application) {
		threadLocal.set(application);
	}

	public void onRequestStart(HttpServletRequest request,
			HttpServletResponse response) {
		GrassApplication.setInstance(this);
	}

	public void onRequestEnd(HttpServletRequest request,
			HttpServletResponse response) {
		threadLocal.remove();
	}

}
