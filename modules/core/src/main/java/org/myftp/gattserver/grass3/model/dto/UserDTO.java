package org.myftp.gattserver.grass3.model.dto;

import java.util.Set;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami. Rozšiřuje
 * {@link UserInfoDTO}, ve kterém jsou všechny "povrchová" data uživatele. Tato
 * třída umí přepravovat i jeho reference jako jsou reference na oblíbené
 * obsahy. Je lepší to takto odlišit - udává to, co všechno se bude tahat z DB.
 * 
 * @author gatt
 * 
 */
public class UserDTO extends UserInfoDTO {

	private static final long serialVersionUID = -5604641536015981637L;

	/**
	 * Oblíbené obsahy
	 */
	private Set<ContentNodeDTO> favourites;

	public Set<ContentNodeDTO> getFavourites() {
		return favourites;
	}

	public void setFavourites(Set<ContentNodeDTO> favourites) {
		this.favourites = favourites;
	}

}
