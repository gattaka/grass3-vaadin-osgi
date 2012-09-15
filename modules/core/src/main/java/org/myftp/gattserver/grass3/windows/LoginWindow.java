package org.myftp.gattserver.grass3.windows;

import org.myftp.gattserver.grass3.GrassApplication;
import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LoginWindow extends OneColumnWindow {

	private static final long serialVersionUID = 8276040419934174157L;

	public static final String NAME = "login";

	public LoginWindow() {
		setName(NAME);
		setCaption("Login");
	}

	@Override
	protected void createContent(HorizontalLayout layout) {

		VerticalLayout loginLayout = new VerticalLayout();
		layout.addComponent(loginLayout);

		// Username
		final TextField username = new TextField("Uživatelské jméno");
		loginLayout.addComponent(username);

		// Password
		final PasswordField password = new PasswordField("Heslo");
		loginLayout.addComponent(password);

		// Login button
		Button loginButton = new Button("Přihlásit",
				new Button.ClickListener() {

					private static final long serialVersionUID = -1805861621517364082L;

					// inline click listener
					public void buttonClick(ClickEvent event) {

						if (((GrassApplication) getApplication()).authenticate(
								username.getValue().toString(),
								password.getValue().toString())) {

							// TODO - zatím jen takhle na main, ale měl by se
							// "vracet" resp. "pokračovat"
							open(new ExternalResource(getApplication().getMainWindow().getURL()));
						} else {
						
							// zobraz chybu
							getWindow().showNotification(
									new Notification("Přihlášení se nezdařilo",
											Notification.TYPE_ERROR_MESSAGE));
						}
					}
				});
		loginLayout.addComponent(loginButton);
		loginLayout.setMargin(true);
		loginLayout.setSpacing(true);
	}
}
