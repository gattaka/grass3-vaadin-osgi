package org.myftp.gattserver.grass3.windows;

import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.facades.UserFacade;
import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class RegistrationWindow extends OneColumnWindow {

	private UserFacade userFacade = UserFacade.INSTANCE;

	private static final long serialVersionUID = 8276040419934174157L;

	private static final int MIN_USERNAME_LENGTH = 2;
	private static final int MAX_USERNAME_LENGTH = 20;

	public RegistrationWindow() {
		setName("registration");
		setCaption("Registrace");
	}

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout formLayout = new VerticalLayout();
		layout.addComponent(formLayout);
		formLayout.addComponent(new Label(
				"<h2>Registrace nového uživatele</h2>", Label.CONTENT_XHTML));

		VerticalLayout formFieldsLayout = new VerticalLayout();
		formLayout.addComponent(formFieldsLayout);
		formFieldsLayout.setSizeFull();
		formFieldsLayout.setSpacing(true);

		final List<Field> fields = new ArrayList<Field>();

		// Username
		final TextField username = new TextField("Uživatelské jméno");
		fields.add(username);
		username.setRequired(true);
		formFieldsLayout.addComponent(username);
		username.addValidator(new StringLengthValidator(
				"Délka jména musí být mezi 2 až 20 znaky", MIN_USERNAME_LENGTH,
				MAX_USERNAME_LENGTH, false));

		// Username
		final TextField email = new TextField("Email");
		fields.add(email);
		email.setRequired(true);
		formFieldsLayout.addComponent(email);
		email.addValidator(new EmailValidator("Email má špatný tvar"));

		// Password
		final PasswordField password = new PasswordField("Heslo");
		fields.add(password);
		password.setRequired(true);
		formFieldsLayout.addComponent(password);

		// Password 2
		final PasswordField password2 = new PasswordField("Heslo znovu");
		fields.add(password2);
		password2.setRequired(true);
		formFieldsLayout.addComponent(password2);

		Validator passValidator = new Validator() {

			private static final long serialVersionUID = 594004281419979661L;

			public void validate(Object value) throws InvalidValueException {
				String passwordValue = (String) password.getValue();
				String password2Value = (String) password2.getValue();
				if (!passwordValue.equals(password2Value))
					throw new InvalidValueException("Hesla se musí shodovat");
			}

			public boolean isValid(Object value) {
				String passwordValue = (String) password.getValue();
				String password2Value = (String) password2.getValue();
				return passwordValue.equals(password2Value);
			}
		};

		password.addValidator(passValidator);
		password2.addValidator(passValidator);

		VerticalLayout buttonLayout = new VerticalLayout();
		formLayout.addComponent(buttonLayout);
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		// Login button
		Button submitButton = new Button("Registrovat",
				new Button.ClickListener() {

					private static final long serialVersionUID = -1805861621517364082L;

					// inline click listener
					public void buttonClick(ClickEvent event) {
						for (Field field : fields) {
							if (!field.isValid()) {
								showWarning("Ve formuláři jsou chybně vyplněné položky");
								return;
							}
						}

						if (userFacade.registrateNewUser(
								(String) email.getValue(),
								(String) username.getValue(),
								(String) password.getValue())) {
							showInfo("Registrace proběhla úspěšně");
							for (Field field : fields) {
								field.setValue("");
							}
						} else {
							showError500();
						}
					}
				});

		buttonLayout.addComponent(submitButton);

		return layout;
	}
}
