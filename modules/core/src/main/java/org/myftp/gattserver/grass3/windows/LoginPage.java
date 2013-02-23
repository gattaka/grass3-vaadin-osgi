package org.myftp.gattserver.grass3.windows;

import org.myftp.gattserver.grass3.GrassUI;
import org.myftp.gattserver.grass3.template.GrassLoginForm;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.template.BasePage;
import org.myftp.gattserver.grass3.windows.template.IPageFactory;
import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.VerticalLayout;

public class LoginPage extends BasePage {

	private static final long serialVersionUID = 8276040419934174157L;

	public static enum LoginPageFactory implements IPageFactory {

		INSTANCE;

		@Override
		public String getPageName() {
			return "login";
		}

		@Override
		public Component createPage(GrassRequest request) {
			return new LoginPage(request);
		}
	}

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout formLayout = new VerticalLayout();
		layout.addComponent(formLayout);
		formLayout.addComponent(new Label("<h2>Přihlášení</h2>",
				Label.CONTENT_XHTML));

		VerticalLayout formFieldsLayout = new VerticalLayout();
		formLayout.addComponent(formFieldsLayout);
		formFieldsLayout.setSizeFull();
		formFieldsLayout.setSpacing(true);

		LoginForm loginForm = new GrassLoginForm();
		loginForm.setUsernameCaption("Jméno");
		loginForm.setPasswordCaption("Heslo");
		loginForm.setLoginButtonCaption("Přihlásit");
		loginForm.addListener(new LoginForm.LoginListener() {

			private static final long serialVersionUID = -3342397991011184546L;

			public void onLogin(LoginEvent event) {
				if (((GrassUI) getApplication()).authenticate(
						event.getLoginParameter("username"),
						event.getLoginParameter("password"))) {

					// TODO - zatím jen takhle na main, ale měl by se
					// "vracet" resp. "pokračovat"
					open(new ExternalResource(getApplication().getMainWindow()
							.getURL()));
				} else {

					// zobraz chybu
					getWindow().showNotification(
							new Notification("Přihlášení se nezdařilo",
									Notification.TYPE_ERROR_MESSAGE));
				}
			}
		});
		formFieldsLayout.addComponent(loginForm);

		return layout;

	}
}
