package org.myftp.gattserver.grass3;

import java.util.Set;

import org.myftp.gattserver.grass3.windows.template.GrassWindow;

/**
 * Objekt sekce. Jde o objekt, který u sebe má informace potřebné k zapojení
 * sekce do systému
 * 
 * @author gatt
 * 
 */
public interface ISection {

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
	public GrassWindow getSectionWindowNewInstance(ServiceHolder serviceHolder);

	/**
	 * Vrátí název sekce, tento text se bude zobrazovat přímo v hlavním menu, ze
	 * kterého se také bude přecházet na okno dané sekce
	 * 
	 * @return název sekce
	 */
	public String getSectionName();

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
