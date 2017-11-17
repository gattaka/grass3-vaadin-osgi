package cz.gattserver.grass3.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.facades.UserFacade;
import cz.gattserver.grass3.interfaces.UserInfoTO;

@Component
public class GrassUserDetailService implements UserDetailsService {

	@Autowired
	private UserFacade userFacade;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final UserInfoTO user = userFacade.getUser(username);
		if (user == null)
			throw new UsernameNotFoundException("Unable to find user");
		return user;
	}

}
