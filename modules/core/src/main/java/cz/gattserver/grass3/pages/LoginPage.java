package cz.gattserver.grass3.pages;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.facades.SecurityFacade;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.ui.H2Label;

public class LoginPage extends OneColumnPage {

	private static final long serialVersionUID = 8276040419934174157L;

	@Resource(name = "homePageFactory")
	private PageFactory homePageFactory;

	@Autowired
	private SecurityFacade securityFacade;

	public LoginPage(GrassRequest request) {
		super(request);
	}

	private boolean login(String username, String password, boolean remember) {
		try {
			securityFacade.login(username, password, remember);
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
				redirect(getPageURL(homePageFactory));
			}
		});
		login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		layout.addComponent(login);

		return marginLayout;

	}
}
