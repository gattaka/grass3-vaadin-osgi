package org.myftp.gattserver.grass3.service;

import java.util.Set;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.myftp.gattserver.grass3.security.Role;

/**
 * Objekt sekce. Jde o objekt, který u sebe má informace potřebné k zapojení
 * sekce do systému
 * 
 * @author gatt
 * 
 */
public interface ISectionService {

	/**
	 * Vrátí factory pro vytváření stránky
	 * 
	 * @return factory stránky
	 */
	public PageFactory getSectionPageFactory();

	/**
	 * Vrátí název sekce, tento text se bude zobrazovat přímo v hlavním menu, ze
	 * kterého se také bude přecházet na okno dané sekce
	 * 
	 * @return název sekce
	 */
	public String getSectionCaption();

	/**
	 * Zjistí, zda může uživatel s danými rolemi zobrazit tuto sekci
	 * 
	 * @param roles
	 *            Role aktuální session, v případě nepřihlášeného uživatele
	 *            {@code null}
	 * @return {@code true} pokud role vyhovují a je možné zobrazovat odkaz na
	 *         tuto sekci, jinak false
	 */
	public boolean isVisibleForRoles(Set<Role> roles);

}
