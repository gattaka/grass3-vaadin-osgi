package cz.gattserver.grass3.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.gattserver.grass3.interfaces.UserInfoTO;

public interface SecurityService {

	public boolean login(String username, String password, boolean remember, HttpServletRequest request,
			HttpServletResponse response);

	public UserInfoTO getCurrentUser();

}
