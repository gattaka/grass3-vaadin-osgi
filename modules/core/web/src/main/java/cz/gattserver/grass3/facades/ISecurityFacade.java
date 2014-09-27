package cz.gattserver.grass3.facades;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import cz.gattserver.grass3.model.dto.UserInfoDTO;

public interface ISecurityFacade {

	public boolean login(String username, String password);

	public UserInfoDTO getCurrentUser();

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException;
}
