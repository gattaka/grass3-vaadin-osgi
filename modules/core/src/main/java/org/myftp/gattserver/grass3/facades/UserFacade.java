package org.myftp.gattserver.grass3.facades;

import java.util.List;

import org.myftp.gattserver.grass3.model.dto.UserDTO;

public class UserFacade {

	// Singleton stuff
	private static UserFacade instance;

	public static UserFacade getInstance() {
		if (instance == null)
			instance = new UserFacade();
		return instance;
	}

	private UserFacade() {
	}

	public List<UserDTO> findUserByUsername(String name) {
		// TODO
		return null;
	}

}
