package org.myftp.gattserver.grass3;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.util.PageFactoriesRegister;
import org.myftp.gattserver.grass3.util.URLPathAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@Title("Gattserver")
@Theme("grass")
@Scope("prototype")
@Component("grassUI")
public class GrassUI extends UI {

	private static final long serialVersionUID = -785347532002801786L;
	private static Logger logger = LoggerFactory.getLogger(GrassUI.class);

	/**
	 * Mapa stránek
	 */
	@Resource(name = "pageFactoriesRegister")
	private PageFactoriesRegister pageFactoriesRegister;

	@Resource(name = "securityFacade")
	private SecurityFacade securityFacade;

	/**
	 * Získá aktuálního přihlášeného uživatele jako {@link UserInfoDTO} objekt
	 */
	public UserInfoDTO getUser() {
		return securityFacade.getCurrentUser();
	}

	public boolean login(String username, String password) {
		return securityFacade.login(username, password);
	}

	public void logout() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(VaadinRequest request) {

		String path = request.getPathInfo();
		logger.info("Path: [" + path + "]");

		GrassRequest grassRequest = new GrassRequest(request);
		URLPathAnalyzer analyzer = grassRequest.getAnalyzer();

		PageFactory factory = pageFactoriesRegister.get(analyzer
				.getPathToken(0));
		setContent(factory.createPage(grassRequest));

	}
}
