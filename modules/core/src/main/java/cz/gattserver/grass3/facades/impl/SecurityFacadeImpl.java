package cz.gattserver.grass3.facades.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.server.VaadinServletService;

import cz.gattserver.grass3.facades.SecurityFacade;
import cz.gattserver.grass3.model.dao.UserRepository;
import cz.gattserver.grass3.model.domain.User;
import cz.gattserver.grass3.model.dto.UserInfoDTO;

@Transactional
@Component
public class SecurityFacadeImpl implements SecurityFacade {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private RememberMeServices rememberMeServices;

	public boolean login(String username, String password, boolean remember) {

		UserInfoDTO principal = new UserInfoDTO();
		principal.setName(username);

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, password);
		token.setDetails(new WebAuthenticationDetails(VaadinServletService.getCurrentServletRequest()));

		try {
			Authentication auth = authenticationManager.authenticate(token);
			if (auth.isAuthenticated()) {
				principal = (UserInfoDTO) auth.getPrincipal();
				SecurityContextHolder.getContext().setAuthentication(auth);
				if (remember && rememberMeServices instanceof TokenBasedRememberMeServices) {
					TokenBasedRememberMeServices rms = (TokenBasedRememberMeServices) rememberMeServices;
					rms.onLoginSuccess(VaadinServletService.getCurrentServletRequest(),
							VaadinServletService.getCurrentResponse().getHttpServletResponse(), auth);
				}
				// zapiš údaj o posledním přihlášení
				User user = userRepository.findOne(principal.getId());
				user.setLastLoginDate(LocalDateTime.now());
				userRepository.save(user);
				return true;
			}
		} catch (BadCredentialsException e) {
		}
		return false;

	}

	public UserInfoDTO getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserInfoDTO) {
				return (UserInfoDTO) principal;
			}
		}
		UserInfoDTO anonUser = new UserInfoDTO();
		return anonUser;
	}

}
