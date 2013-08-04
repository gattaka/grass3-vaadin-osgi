package org.myftp.gattserver.grass3.pages;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.pages.template.OneColumnPage;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component("loginPage")
@Scope("prototype")
public class LoginPage extends OneColumnPage {

	private static final long serialVersionUID = 8276040419934174157L;

	@Resource(name = "homePageFactory")
	private IPageFactory homePageFactory;

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

		formLayout
				.addComponent(new Label(
						"<form name='f' autocomplete='on' action='j_spring_security_check' method='POST'>"
								+ "<table>"
								+ "<tr>"
								+ "<td>Jméno:</td>"
								+ "<td><input type='text' name='j_username' value=''></td>"
								+ "</tr>"
								+ "<tr>"
								+ "<td>Heslo:</td>"
								+ "<td><input type='password' name='j_password' /></td>"
								+ "</tr>"
								+ "<tr>"
								+ "<td>Zapamatovat:</td>"
								+ "<td><input type='checkbox' name='_spring_security_remember_me' /></td>"
								+ "</tr>"
								+ "<tr>"
								+ "<td><input name='submit' type='submit' value='Přihlásit' /></td>"
								+ "</tr>" + "</table>" + "</form>",
						ContentMode.HTML));

		return layout;

	}
}
