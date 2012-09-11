package org.myftp.gattserver.grass3;

import org.myftp.gattserver.grass3.model.dto.UserDTO;

/**
 * Objekt, ukládající informace o aktuálním zabezpečení
 * 
 * @author gatt
 * 
 */
public class SecurityStore {

	private UserDTO loggedUser;

	public UserDTO getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(UserDTO loggedUser) {
		this.loggedUser = loggedUser;
	}

}
