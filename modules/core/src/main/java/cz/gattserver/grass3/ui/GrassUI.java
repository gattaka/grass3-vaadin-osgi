package cz.gattserver.grass3.ui;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.modules.register.PageFactoriesRegister;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;
import cz.gattserver.web.common.server.URLPathAnalyzer;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.exception.ApplicationErrorHandler;

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
	private SecurityService securityFacade;

	@Resource(name = "err403PageFactory")
	private PageFactory err403PageFactory;

	@Resource(name = "err404PageFactory")
	private PageFactory err404PageFactory;

	@Resource(name = "err500PageFactory")
	private PageFactory err500PageFactory;

	public GrassUI() {
		SpringContextHelper.inject(this);
	}

	/**
	 * Získá aktuálního přihlášeného uživatele jako {@link UserInfoTO} objekt
	 */
	public UserInfoTO getUser() {
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

		try {
			GrassPage page = factory.createPageIfAuthorized(grassRequest);
			Component content = page.getContent();
			setContent(content);
		} catch (GrassPageException e) {
			switch (e.getStatus()) {
			case 403:
				setContent(err403PageFactory.createPageIfAuthorized(grassRequest).getContent());
				break;
			case 404:
				setContent(err404PageFactory.createPageIfAuthorized(grassRequest).getContent());
				break;
			case 500:
			default:
				setContent(err500PageFactory.createPageIfAuthorized(grassRequest).getContent());
				break;
			}
		}

	}
}