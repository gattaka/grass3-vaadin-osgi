package org.myftp.gattserver.grass3.service;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.windows.template.GrassWindow;

/**
 * Objekt nastavení. Jde o objekt, který u sebe má informace potřebné k zapojení
 * okna nastavení do systému
 * 
 * @author gatt
 * 
 */
public interface ISettingsService {

	/**
	 * Vrátí novou instanci okna nastavení
	 * 
	 * @return okno nastavení
	 */
	public GrassWindow getSettingsWindowNewInstance();

	/**
	 * Vrátí třídu nastavení
	 * 
	 * @return třída nastavení
	 */
	public Class<? extends GrassWindow> getSettingsWindowClass();

	/**
	 * Vrátí název okna nastavení, tento text se bude zobrazovat v menu, ze
	 * kterého se také bude přecházet na okno daného nastavení
	 * 
	 * @return název nastavení
	 */
	public String getSectionCaption();

	/**
	 * Zjistí, zda má být zobrazen odkaz na toto nastavení
	 * 
	 * @param roles
	 *            Role aktuální session
	 * @return {@code true} pokud role vyhovují a je možné zobrazovat odkaz na
	 *         toto nastavení, jinak false
	 */
	public boolean isVisibleForRoles(Set<Role> roles);

}
