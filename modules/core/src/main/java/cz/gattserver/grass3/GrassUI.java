package cz.gattserver.grass3;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.exception.ApplicationErrorHandler;
import cz.gattserver.grass3.facades.SecurityFacade;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.pages.template.GrassLayout;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.grass3.ui.util.PageFactoriesRegister;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.URLPathAnalyzer;

@Title("Gattserver")
@Theme("grass")
public class GrassUI extends UI {

	private static final long serialVersionUID = -785347532002801786L;
	private static Logger logger = LoggerFactory.getLogger(GrassUI.class);

	/**
	 * Mapa stránek
	 */
	@Autowired
	private PageFactoriesRegister pageFactoriesRegister;

	@Autowired
	private SecurityFacade securityFacade;

	@Resource(name = "err403Factory")
	private PageFactory err403Factory;

	@Resource(name = "err404Factory")
	private PageFactory err404Factory;

	@Resource(name = "err500Factory")
	private PageFactory err500Factory;

	public GrassUI() {
		SpringContextHelper.inject(this);
	}

	/**
	 * Získá aktuálního přihlášeného uživatele jako {@link UserInfoDTO} objekt
	 */
	public UserInfoDTO getUser() {
		return securityFacade.getCurrentUser();
	}

	public void init(VaadinRequest request) {

		VaadinSession.getCurrent().setErrorHandler(new ApplicationErrorHandler());

		String path = request.getPathInfo();
		String contextPath = request.getContextPath();
		logger.info("Context Path: [" + contextPath + "]");
		logger.info("Path: [" + path + "]");

		GrassRequest grassRequest = new GrassRequest(request);
		URLPathAnalyzer analyzer = grassRequest.getAnalyzer();

		// pokud nebyla cesta prázná, pak proveď posuv
		String token = analyzer.getNextPathToken();
		PageFactory factory = pageFactoriesRegister.get(token);

		GrassLayout buildedPage = factory.createPageIfAuthorized(grassRequest);

		switch (grassRequest.getPageState()) {
		case CLEAN:
			setContent(buildedPage);
			break;
		case E403:
			setContent(err403Factory.createPageIfAuthorized(grassRequest));
			break;
		case E404:
			setContent(err404Factory.createPageIfAuthorized(grassRequest));
			break;
		case E500:
			setContent(err500Factory.createPageIfAuthorized(grassRequest));
			break;
		}

	}
}