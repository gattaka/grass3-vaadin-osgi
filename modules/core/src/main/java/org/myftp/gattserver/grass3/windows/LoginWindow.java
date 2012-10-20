package org.myftp.gattserver.grass3.windows;

import org.myftp.gattserver.grass3.GrassApplication;
import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
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
	protected void createContent(VerticalLayout layout) {

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout formLayout = new VerticalLayout();
		layout.addComponent(formLayout);
		formLayout.addComponent(new Label(
				"<h2>Přihlášení</h2>", Label.CONTENT_XHTML));

		VerticalLayout formFieldsLayout = new VerticalLayout();
		formLayout.addComponent(formFieldsLayout);
		formFieldsLayout.setSizeFull();
		formFieldsLayout.setSpacing(true);
		
		// Username
		final TextField username = new TextField("Uživatelské jméno");
		formFieldsLayout.addComponent(username);

		// Password
		final PasswordField password = new PasswordField("Heslo");
		formFieldsLayout.addComponent(password);

		// Login button
		Button loginButton = new Button("Přihlásit",
				new Button.ClickListener() {

					private static final long serialVersionUID = -1805861621517364082L;

					// inline click listener
					public void buttonClick(ClickEvent event) {

						if (((GrassApplication) getApplication()).authenticate(
								username.getValue().toString(), password
										.getValue().toString())) {

							// TODO - zatím jen takhle na main, ale měl by se
							// "vracet" resp. "pokračovat"
							open(new ExternalResource(getApplication()
									.getMainWindow().getURL()));
						} else {

							// zobraz chybu
							getWindow().showNotification(
									new Notification("Přihlášení se nezdařilo",
											Notification.TYPE_ERROR_MESSAGE));
						}
					}
				});
		formFieldsLayout.addComponent(loginButton);

	}
}
