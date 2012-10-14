package org.myftp.gattserver.grass3.service;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.windows.template.GrassWindow;

/**
 * Objekt sekce. Jde o objekt, který u sebe má informace potřebné k zapojení
 * sekce do systému
 * 
 * @author gatt
 * 
 */
public interface ISectionService {

	/**
	 * Vrátí třídu okna, které poskytuje jako sekce
	 * 
	 * @return třída okna
	 */
	public Class<? extends GrassWindow> getSectionWindowClass();

	/**
	 * Vrátí novou instanci okna sekce
	 * 
	 * @return nové okno sekce
	 */
	public GrassWindow getSectionWindowNewInstance();

	/**
	 * Vrátí název sekce, tento text se bude zobrazovat přímo v hlavním menu, ze
	 * kterého se také bude přecházet na okno dané sekce
	 * 
	 * @return název sekce
	 */
	public String getSectionCaption();

	/**
	 * Vrátí identifikátor sekce, podle tohoto jména se bude sekce hledat v
	 * seznamu oken
	 * 
	 * @return název sekce
	 */
	public String getSectionIDName();

	/**
	 * Zjistí, zda má být zobrazen odkaz na tuto sekci
	 * 
	 * @param roles
	 *            Role aktuální session
	 * @return {@code true} pokud role vyhovují a je možné zobrazovat odkaz na
	 *         tuto sekci, jinak false
	 */
	public boolean isVisibleForRoles(Set<Role> roles);

}
