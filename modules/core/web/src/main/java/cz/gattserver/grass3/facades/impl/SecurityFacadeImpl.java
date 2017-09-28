package cz.gattserver.grass3.facades.impl;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.facades.SecurityFacade;
import cz.gattserver.grass3.facades.UserFacade;
import cz.gattserver.grass3.model.dao.UserRepository;
import cz.gattserver.grass3.model.domain.User;
import cz.gattserver.grass3.model.dto.UserInfoDTO;

@Transactional
@Component
public class SecurityFacadeImpl implements SecurityFacade {

	@Autowired
	private UserFacade userFacade;

	@Autowired
	private UserRepository userRepository;

	public boolean login(String username, String password) {
		UserInfoDTO loggedUser = userFacade.getUserByLogin(username, password);
		if (loggedUser == null || loggedUser.isConfirmed() == false)
			return false;

		Authentication authentication = new UsernamePasswordAuthenticationToken(loggedUser, loggedUser.getPassword(),
				loggedUser.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		// zapiš údaj o posledním přihlášení
		User user = userRepository.findOne(loggedUser.getId());
		user.setLastLoginDate(Calendar.getInstance().getTime());
		userRepository.save(user);
		return true;
	}

	public UserInfoDTO getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserInfoDTO) {
			return (UserInfoDTO) principal;
		} else {
			UserInfoDTO anonUser = new UserInfoDTO();
			return anonUser;
		}
	}

}
