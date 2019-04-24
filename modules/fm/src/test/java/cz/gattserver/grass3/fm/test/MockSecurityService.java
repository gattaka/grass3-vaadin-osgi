package cz.gattserver.grass3.fm.test;

import java.util.Set;

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

	private Set<Role> roles;

	@Override
	public LoginResult login(String username, String password, boolean remember, HttpServletRequest request,
			HttpServletResponse response) {
		return null;
	}

	@Override
	public UserInfoTO getCurrentUser() {
		UserInfoTO mockTO = new UserInfoTO();
		mockTO.setName("mockUser");
		mockTO.setRoles(roles);
		return mockTO;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
	}

}
