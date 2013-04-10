package org.myftp.gattserver.grass3;

import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.ISecurityFacade;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.util.IGrassRequestHandler;
import org.myftp.gattserver.grass3.util.IPageFactoriesRegister;
import org.myftp.gattserver.grass3.util.URLPathAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
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
	private IPageFactoriesRegister pageFactoriesRegister;

	@Resource(name = "securityFacade")
	private ISecurityFacade securityFacade;

	@Autowired
	private List<IGrassRequestHandler> grassRequestHandlers;

	/**
	 * Získá aktuálního přihlášeného uživatele jako {@link UserInfoDTO} objekt
	 */
	public UserInfoDTO getUser() {
		return securityFacade.getCurrentUser();
	}

	public boolean login(String username, String password) {
		return securityFacade.login(username, password);
	}

	public void init(VaadinRequest request) {

		for (IGrassRequestHandler handler : grassRequestHandlers) {
			VaadinSession.getCurrent().addRequestHandler(handler);
		}

		String path = request.getPathInfo();
		String contextPath = request.getContextPath();
		logger.info("Context Path: [" + contextPath + "]");
		logger.info("Path: [" + path + "]");

		GrassRequest grassRequest = new GrassRequest(request);
		URLPathAnalyzer analyzer = grassRequest.getAnalyzer();

		// pokud nebyla cesta prázná, pak proveď posuv
		if (analyzer.getPathToken(0) != null)
			analyzer.shift();

		IPageFactory factory = pageFactoriesRegister.get(analyzer
				.getPathToken(0));
		setContent(factory.createPage(grassRequest));

	}
}
