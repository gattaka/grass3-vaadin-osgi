package org.myftp.gattserver.grass3.windows;

import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.ifces.PageFactory;
import org.myftp.gattserver.grass3.windows.template.BasePage;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LoginPage extends BasePage {

	private static final long serialVersionUID = 8276040419934174157L;

	public static enum LoginPageFactory implements PageFactory {

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

	public LoginPage(GrassRequest request) {
		super(request);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createContent(CustomLayout layout) {

		CustomLayout contentLayout = new CustomLayout("oneColumn");
		layout.addComponent(contentLayout, "content");

		VerticalLayout pagelayout = new VerticalLayout();

		pagelayout.setMargin(true);
		pagelayout.setSpacing(true);

		VerticalLayout formLayout = new VerticalLayout();
		pagelayout.addComponent(formLayout);
		formLayout.addComponent(new Label("<h2>Přihlášení</h2>",
				ContentMode.HTML));

		VerticalLayout formFieldsLayout = new VerticalLayout();
		formLayout.addComponent(formFieldsLayout);
		formFieldsLayout.setSizeFull();
		formFieldsLayout.setSpacing(true);

		final TextField nameField = new TextField("Jméno");
		final TextField passField = new TextField("Heslo");
		formFieldsLayout.addComponent(nameField);
		formFieldsLayout.addComponent(passField);
		formFieldsLayout.addComponent(new Button("Přihlásit",
				new Button.ClickListener() {

					private static final long serialVersionUID = 256710217389298911L;

					@Override
					public void buttonClick(ClickEvent event) {
						if (getGrassUI().login(nameField.getValue(),
								passField.getValue())) {

							// TODO - zatím jen takhle na main, ale měl by se
							// "vracet" resp. "pokračovat"
							redirect("/");
						} else {

							// zobraz chybu
							showError("Přihlášení se nezdařilo");
						}
					}
				}));

		contentLayout.addComponent(pagelayout, "content");

	}
}
