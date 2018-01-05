package cz.gattserver.grass3.pg.test;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.services.impl.LoginResult;

@Service
@Primary
public class MockSecurityService implements SecurityService {

	private UserInfoTO infoTO;

	public MockSecurityService() {
		infoTO = new UserInfoTO();
		infoTO.setName("mockUser");
		infoTO.setRoles(new HashSet<>());
		infoTO.setId(33333L);
	}

	@Override
	public LoginResult login(String username, String password, boolean remember, HttpServletRequest request,
			HttpServletResponse response) {
		return null;
	}

	@Override
	public UserInfoTO getCurrentUser() {
		return infoTO;
	}

	public UserInfoTO getInfoTO() {
		return infoTO;
	}

	public void setInfoTO(UserInfoTO infoTO) {
		this.infoTO = infoTO;
	}

	public void setRoles(HashSet<Role> hashSet) {
		infoTO.setRoles(hashSet);
	}

}
