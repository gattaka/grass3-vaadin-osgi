package cz.gattserver.grass3.facades;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.gattserver.grass3.interfaces.UserInfoTO;

public interface SecurityFacade {

	public boolean login(String username, String password, boolean remember, HttpServletRequest request,
			HttpServletResponse response);

	public UserInfoTO getCurrentUser();

}
