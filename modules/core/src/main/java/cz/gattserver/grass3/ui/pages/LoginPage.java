package cz.gattserver.grass3.ui.pages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.services.impl.LoginResult;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.H2Label;

public class LoginPage extends OneColumnPage {

	@Autowired
	private SecurityService securityFacade;

	public LoginPage(GrassRequest request) {
		super(request);
	}

	private LoginResult login(String username, String password, boolean remember) {
		HttpServletRequest request = VaadinServletService.getCurrentServletRequest();
		HttpServletResponse response = VaadinServletService.getCurrentResponse().getHttpServletResponse();
		LoginResult loginResult = securityFacade.login(username, password, remember, request, response);
		if (LoginResult.SUCCESS == loginResult) {
			// Reinitialize the session to protect against session fixation
			// attacks. This does not work with websocket communication.
			VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
		}
		return loginResult;
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
			LoginResult loginResult = login(username.getValue(), pword, rememberMe.getValue());
			switch (loginResult) {
			case FAILED_CREDENTIALS:
				UIUtils.showError("Špatné přihlašovací jméno nebo heslo");
				username.focus();
				break;
			case FAILED_DISABLED:
				UIUtils.showError("Účet je deaktivován");
				break;
			case FAILED_LOCKED:
				UIUtils.showError("Účet je zamčen");
				break;
			case SUCCESS:
				UIUtils.redirect(getPageURL(homePageFactory));
				break;
			}
		});
		login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		layout.addComponent(login);

		return marginLayout;
	}
}
