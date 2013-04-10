package org.myftp.gattserver.grass3.facades.impl;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.ISecurityFacade;
import org.myftp.gattserver.grass3.facades.IUserFacade;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityFacade")
public class SecurityFacadeImpl implements ISecurityFacade {

	@Resource(name = "userFacade")
	private IUserFacade userFacade;

	public boolean login(String username, String password) {
		UserInfoDTO loggedUser = userFacade.getUserByLogin(username, password);
		if (loggedUser == null || loggedUser.isConfirmed() == false)
			return false;

		Authentication authentication = new UsernamePasswordAuthenticationToken(
				loggedUser, loggedUser.getPassword(),
				loggedUser.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
		return true;
	}

	public UserInfoDTO getCurrentUser() {

		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		if (principal instanceof UserInfoDTO)
			return (UserInfoDTO) principal;
		else
			return null;
	}

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		if (!(authentication.getPrincipal() instanceof String)
				|| (!(authentication.getCredentials() instanceof String)))
			throw new AuthenticationException("Authentication failed") {
				private static final long serialVersionUID = 1622317305057326834L;
			};

		String username = (String) authentication.getPrincipal();
		String password = (String) authentication.getCredentials();

		UserInfoDTO loggedUser = userFacade.getUserByLogin(username, password);
		if (loggedUser == null)
			throw new BadCredentialsException("Bad credentials");

		authentication.setAuthenticated(true);
		return authentication;
	}
}
