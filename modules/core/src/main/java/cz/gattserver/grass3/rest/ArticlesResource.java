package cz.gattserver.grass3.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.services.impl.LoginResult;

@Controller
@RequestMapping("/core")
public class ArticlesResource {

	@Autowired
	private SecurityService securityFacade;

	@RequestMapping("/logged")
	public @ResponseBody String log() {
		UserInfoTO user = securityFacade.getCurrentUser();
		return user.getName() == null ? "unauth" : user.getName();
	}

	// curl -i -X POST -d login=jmeno -d password=heslo
	// http://localhost:8180/web/ws/pg/login
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<String> login(@RequestParam("login") String username,
			@RequestParam("password") String password, HttpServletRequest request, HttpServletResponse response) {
		LoginResult result = securityFacade.login(username, password, false, request, response);
		if (LoginResult.SUCCESS.equals(result)) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}

}
