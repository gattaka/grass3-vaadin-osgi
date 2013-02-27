package org.myftp.gattserver.grass3.service;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.windows.template.SettingsPageFactory;

/**
 * Objekt nastavení. Jde o objekt, který u sebe má informace potřebné k zapojení
 * okna nastavení do systému
 * 
 * @author gatt
 * 
 */
public interface ISettingsService {

	/**
	 * Vrátí factory pro vytváření stránky nastavení
	 * 
	 * @return factory stránky nastavení
	 */
	public SettingsPageFactory getSettingsPageFactory();

	/**
	 * Vrátí název okna nastavení, tento text se bude zobrazovat v menu, ze
	 * kterého se také bude přecházet na okno daného nastavení
	 * 
	 * @return název nastavení
	 */
	public String getSettingsCaption();

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
