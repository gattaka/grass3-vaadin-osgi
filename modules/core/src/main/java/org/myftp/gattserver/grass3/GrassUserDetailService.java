package org.myftp.gattserver.grass3;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.IUserFacade;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("grassUserDetailService")
public class GrassUserDetailService implements UserDetailsService {

	@Resource(name = "userFacade")
	private IUserFacade userFacade;

	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {

		final UserInfoDTO user = userFacade.getUser(username);
		if (user == null) {
			throw new UsernameNotFoundException("Unable to find user");
		}

		return user;
	}

}
