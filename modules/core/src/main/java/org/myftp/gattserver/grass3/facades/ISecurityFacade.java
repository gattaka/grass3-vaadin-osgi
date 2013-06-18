package org.myftp.gattserver.grass3.facades;

import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface ISecurityFacade {

	public boolean login(String username, String password);

	public UserInfoDTO getCurrentUser();

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException;
}
