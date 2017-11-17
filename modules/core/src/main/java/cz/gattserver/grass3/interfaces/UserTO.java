package cz.gattserver.grass3.interfaces;

import java.util.Set;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami. Rozšiřuje
 * {@link UserInfoTO}, ve kterém jsou všechny "povrchová" data uživatele. Tato
 * třída umí přepravovat i jeho reference jako jsou reference na oblíbené
 * obsahy. Je lepší to takto odlišit - udává to, co všechno se bude tahat z DB.
 * 
 * @author gatt
 * 
 */
public class UserTO extends UserInfoTO {

	private static final long serialVersionUID = -5604641536015981637L;

	/**
	 * Oblíbené obsahy
	 */
	private Set<ContentNodeTO> favourites;

	public Set<ContentNodeTO> getFavourites() {
		return favourites;
	}

	public void setFavourites(Set<ContentNodeTO> favourites) {
		this.favourites = favourites;
	}

}
