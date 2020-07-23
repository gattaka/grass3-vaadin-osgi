package cz.gattserver.grass3.ui.pages.template;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.server.DefaultErrorHandler;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.theme.Theme;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.js.JScriptItem;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.exception.ExceptionDialog;

/**
 * Základní layout pro stránky systému Grass. Volá {@link SpringContextHelper}
 * pro injektování závislostí. Poskytuje metody pro vyhazování chyb na stránce,
 * přidávání JS a CSS zdrojů a získávání URL informací.
 *
 * Anotace {@link CssImport}, {@link JsModule} a {@link Theme} jsou ve web
 * modulu (aby fungoval incremental build)
 * 
 * @author Hynek
 *
 */
public abstract class GrassPage extends Div implements PageConfigurator {

	private static final long serialVersionUID = 7952966362953000385L;

	private static final Logger logger = LoggerFactory.getLogger(GrassPage.class);

	private transient SecurityService securityFacade;

	/**
	 * Konstruktor stránky. Slouží pro přípravu dat pro její sestavení, ale sám
	 * ještě nesestavuje.
	 * 
	 * @param request
	 *            {@link GrassRequest}, v rámci kterého je stránka vystavována
	 */
	public GrassPage() {
		SpringContextHelper.inject(this);
		if (UI.getCurrent().getSession().getErrorHandler() == null
				|| UI.getCurrent().getSession().getErrorHandler() instanceof DefaultErrorHandler)
			UI.getCurrent().getSession().setErrorHandler(e -> {
				logger.error("V aplikaci došlo k chybě", e.getThrowable());
				new ExceptionDialog(e.getThrowable()).open();
			});
	}

	@Override
	public void configurePage(InitialPageSettings settings) {
		settings.addFavIcon("icon", "img/favicon.png", "16px");
	}

	public void init() {
		createPageElements(this);
		setId("main-div");
		UI.getCurrent().getPage().addJavaScript("context://js/jquery.js");
	}

	protected abstract void createPageElements(Div div);

	/**
	 * Získá aktuálního přihlášeného uživatele jako {@link UserInfoTO} objekt
	 */
	public UserInfoTO getUser() {
		if (securityFacade == null)
			securityFacade = SpringContextHelper.getBean(SecurityService.class);
		return securityFacade.getCurrentUser();
	}

	public PendingJavaScriptResult loadJS(JScriptItem... scripts) {
		return UIUtils.loadJS(scripts);
	}

	public PendingJavaScriptResult loadJS(List<JScriptItem> scripts) {
		return UIUtils.loadJS(scripts);
	}

	public String getContextPath() {
		return UIUtils.getContextPath();
	}

	public String getPageURL(PageFactory pageFactory) {
		return UIUtils.getPageURL(pageFactory);
	}

	public String getPageURL(String suffix) {
		return UIUtils.getPageURL(suffix);
	}

	public String getPageURL(PageFactory pageFactory, String... relativeURLs) {
		return UIUtils.getPageURL(pageFactory, relativeURLs);
	}

	public void loadCSS(String link) {
		UIUtils.loadCSS(link);
	}

}
