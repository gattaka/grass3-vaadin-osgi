package cz.gattserver.grass3.ui.pages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletService;

import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.services.impl.LoginResult;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.UIUtils;

@Route(value = "loginpage")
public class LoginPage extends OneColumnPage {

	private static final long serialVersionUID = 2568522523298977106L;

	@Autowired
	private SecurityService securityFacade;

	public LoginPage() {
		init();
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
	protected void createColumnContent(Div layout) {
		layout.add(new H2("Přihlášení"));

		FormLayout formLayout = new FormLayout();
		layout.add(formLayout);

		TextField username = new TextField("Login");
		formLayout.add(username);

		PasswordField password = new PasswordField("Heslo");
		formLayout.add(password);

		Checkbox rememberMe = new Checkbox("Zapamatovat si přihlášení");
		formLayout.add(rememberMe);

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
		login.addClickShortcut(Key.ENTER);
		formLayout.add(login);
	}
}
