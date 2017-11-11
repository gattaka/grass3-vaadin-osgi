package cz.gattserver.grass3.pages;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.facades.UserFacade;
import cz.gattserver.grass3.pages.template.OneColumnPage;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.H2Label;

public class RegistrationPage extends OneColumnPage {

	@Autowired
	private UserFacade userFacade;

	private static final int MIN_USERNAME_LENGTH = 2;
	private static final int MAX_USERNAME_LENGTH = 20;

	public RegistrationPage(GrassRequest request) {
		super(request);
	}

	private static class RegistrationTO {
		private String username;
		private String password;
		private String password2;
		private String email;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getPassword2() {
			return password2;
		}

		public void setPassword2(String password2) {
			this.password2 = password2;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout formLayout = new VerticalLayout();
		layout.addComponent(formLayout);
		formLayout.addComponent(new H2Label("Registrace nového uživatele"));

		GridLayout formFieldsLayout = new GridLayout(2, 4);
		formLayout.addComponent(formFieldsLayout);
		formFieldsLayout.setSpacing(true);
		formFieldsLayout.setMargin(false);

		Binder<RegistrationTO> binder = new Binder<>();
		binder.setBean(new RegistrationTO());

		// Username
		final TextField usernameField = new TextField("Uživatelské jméno");
		binder.forField(usernameField).asRequired("Jméno je povinné")
				.withValidator(new StringLengthValidator("Délka jména musí být mezi 2 až 20 znaky", MIN_USERNAME_LENGTH,
						MAX_USERNAME_LENGTH))
				.bind(RegistrationTO::getUsername, RegistrationTO::setUsername);
		formFieldsLayout.addComponent(usernameField, 0, 0);

		// Email
		final TextField emailField = new TextField("Email");
		binder.forField(emailField).asRequired("Email je povinný")
				.withValidator(new EmailValidator("Email má špatný tvar"))
				.bind(RegistrationTO::getEmail, RegistrationTO::setEmail);
		formFieldsLayout.addComponent(emailField, 0, 1);

		// Password
		final PasswordField passwordField = new PasswordField("Heslo");
		binder.forField(passwordField).asRequired("Heslo je povinné").bind(RegistrationTO::getPassword,
				RegistrationTO::setPassword);
		formFieldsLayout.addComponent(passwordField, 0, 2);

		// Password 2
		final PasswordField passwordCopyField = new PasswordField("Heslo znovu");
		binder.forField(passwordCopyField).asRequired("Heslo je povinné")
				.withValidator((String value, ValueContext context) -> {
					if (binder.getBean().getPassword() != null && binder.getBean().getPassword().equals(value))
						return ValidationResult.ok();
					return ValidationResult.error("Hesla se musí shodovat");
				}).bind(RegistrationTO::getPassword2, RegistrationTO::setPassword2);
		formFieldsLayout.addComponent(passwordCopyField, 0, 3);

		VerticalLayout buttonLayout = new VerticalLayout();
		formLayout.addComponent(buttonLayout);
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(false);

		// Login button
		Button submitButton = new Button("Registrovat", event -> {
			if (binder.isValid()) {
				RegistrationTO bean = binder.getBean();
				userFacade.registrateNewUser(bean.getEmail(), bean.getUsername(), bean.getPassword());
				UIUtils.showInfo("Registrace proběhla úspěšně");
				binder.setBean(new RegistrationTO());
			} else {

			}
		});
		submitButton.setEnabled(false);
		buttonLayout.addComponent(submitButton);

		binder.addStatusChangeListener(e -> submitButton.setEnabled(e.getBinder().isValid()));

		return layout;
	}
}
