package cz.gattserver.grass3.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.services.impl.LoginResult;

public interface SecurityService {

	public LoginResult login(String username, String password, boolean remember, HttpServletRequest request,
			HttpServletResponse response);

	public UserInfoTO getCurrentUser();

}
