package cz.gattserver.grass3.ui.pages;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.H2Label;

public class LoginPage extends OneColumnPage {

	@Resource(name = "homePageFactory")
	private PageFactory homePageFactory;

	@Autowired
	private SecurityService securityFacade;

	public LoginPage(GrassRequest request) {
		super(request);
	}

	private boolean login(String username, String password, boolean remember) {
		try {
			HttpServletRequest request = VaadinServletService.getCurrentServletRequest();
			HttpServletResponse response = VaadinServletService.getCurrentResponse().getHttpServletResponse();
			securityFacade.login(username, password, remember, request, response);
			// Reinitialize the session to protect against session fixation
			// attacks. This does not work
			// with websocket communication.
			VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
			return true;
		} catch (AuthenticationException ex) {
			return false;
		}
	}

	@Override
	protected Component createContent() {

		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setMargin(true);

		VerticalLayout layout = new VerticalLayout();
		marginLayout.addComponent(layout);
		layout.setMargin(true);
		layout.setSpacing(true);

		layout.addComponent(new H2Label("Přihlášení"));

		TextField username = new TextField("Login");
		layout.addComponent(username);

		PasswordField password = new PasswordField("Heslo");
		layout.addComponent(password);

		CheckBox rememberMe = new CheckBox("Zapamatovat si přihlášení");
		layout.addComponent(rememberMe);

		Button login = new Button("Přihlásit", evt -> {
			String pword = password.getValue();
			password.setValue("");
			if (!login(username.getValue(), pword, rememberMe.getValue())) {
				Notification.show("Přihlášení se nezdařilo");
				username.focus();
			} else {
				UIUtils.redirect(getPageURL(homePageFactory));
			}
		});
		login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		layout.addComponent(login);

		return marginLayout;

	}
}
