package org.myftp.gattserver.grass3.util;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Link;

/**
 * Speciální úprava Vaadin link elementu pro tabulky, které potřebují, aby byl
 * typ v daném sloupci {@link Comparable} jinak sloupec nejde řadit (nelze řadit
 * podle něj) - v případě linku by přitom stačilo porovnávat jenom název.
 * 
 * @author Gattaka
 * 
 */
public class ComparableLink extends Link implements Comparable<ComparableLink> {

	private static final long serialVersionUID = -1066469018592736445L;

	public ComparableLink(String name, ExternalResource externalResource) {
		super(name, externalResource);
	}

	public int compareTo(ComparableLink o) {
		return this.getCaption().compareTo(o.getCaption());
	}

}
