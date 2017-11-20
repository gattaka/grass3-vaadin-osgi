package cz.gattserver.grass3.facades;

import cz.gattserver.grass3.interfaces.UserInfoTO;

public interface SecurityFacade {

	public boolean login(String username, String password, boolean remember);

	public UserInfoTO getCurrentUser();

}
