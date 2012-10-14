package org.myftp.gattserver.grass3.service;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.windows.template.SettingsWindow;

public interface ISettingsService extends IModuleService {

	/**
	 * Vrátí třídu stránky, která slouží k nastavení daného modulu, může být i
	 * {@code null}
	 * 
	 * @return třída stránky nebo null pokud modul nemá žádná nastavení
	 */
	public Class<? extends SettingsWindow> getModuleSettingsPageClass();

	/**
	 * Vrátí role uživatelů, kteří mohou nastavovat toto nastavení
	 * 
	 * @return role, které mohou toto nastavení vidět
	 */
	public Set<Role> getSettingsAllowedRoles();

}
