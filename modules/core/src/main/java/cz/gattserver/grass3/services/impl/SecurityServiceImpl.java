package cz.gattserver.grass3.services.impl;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.model.domain.User;
import cz.gattserver.grass3.model.repositories.UserRepository;
import cz.gattserver.grass3.services.SecurityService;

@Transactional
@Service
public class SecurityServiceImpl implements SecurityService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private RememberMeServices rememberMeServices;

	public LoginResult login(String username, String password, boolean remember, HttpServletRequest request,
			HttpServletResponse response) {

		UserInfoTO principal = new UserInfoTO();
		principal.setName(username);

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, password);
		token.setDetails(new WebAuthenticationDetails(request));

		try {
			Authentication auth = authenticationManager.authenticate(token);
			if (auth.isAuthenticated()) {
				principal = (UserInfoTO) auth.getPrincipal();
				SecurityContextHolder.getContext().setAuthentication(auth);
				if (remember && rememberMeServices instanceof TokenBasedRememberMeServices) {
					TokenBasedRememberMeServices rms = (TokenBasedRememberMeServices) rememberMeServices;
					rms.onLoginSuccess(request, response, auth);
				}
				// zapiš údaj o posledním přihlášení
				User user = userRepository.findOne(principal.getId());
				user.setLastLoginDate(LocalDateTime.now());
				userRepository.save(user);
			}
		} catch (BadCredentialsException e) {
			return LoginResult.FAILED_CREDENTIALS;
		} catch (DisabledException e) {
			return LoginResult.FAILED_DISABLED;
		} catch (LockedException e) {
			return LoginResult.FAILED_LOCKED;
		}
		return LoginResult.SUCCESS;
	}

	public UserInfoTO getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserInfoTO) {
				return (UserInfoTO) principal;
			}
		}
		UserInfoTO anonUser = new UserInfoTO();
		return anonUser;
	}

}