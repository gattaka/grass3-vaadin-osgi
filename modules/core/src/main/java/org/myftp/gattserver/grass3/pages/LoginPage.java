package org.myftp.gattserver.grass3.pages;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.myftp.gattserver.grass3.pages.template.OneColumnPage;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component("loginPage")
@Scope("prototype")
public class LoginPage extends OneColumnPage {

	private static final long serialVersionUID = 8276040419934174157L;

	@Resource(name = "homePageFactory")
	private PageFactory homePageFactory;
	
	public LoginPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout formLayout = new VerticalLayout();
		layout.addComponent(formLayout);
		formLayout.addComponent(new Label("<h2>Přihlášení</h2>",
				ContentMode.HTML));

		VerticalLayout formFieldsLayout = new VerticalLayout();
		formLayout.addComponent(formFieldsLayout);
		formFieldsLayout.setSizeFull();
		formFieldsLayout.setSpacing(true);

		final TextField nameField = new TextField("Jméno");
		final PasswordField passField = new PasswordField("Heslo");
		formFieldsLayout.addComponent(nameField);
		formFieldsLayout.addComponent(passField);
		formFieldsLayout.addComponent(new Button("Přihlásit",
				new Button.ClickListener() {

					private static final long serialVersionUID = 256710217389298911L;

					public void buttonClick(ClickEvent event) {
						if (getGrassUI().login(nameField.getValue(),
								passField.getValue())) {
							redirect(getPageURL(homePageFactory));
						} else {
							showError("Přihlášení se nezdařilo");
						}
					}
				}));

		return layout;

	}
}
