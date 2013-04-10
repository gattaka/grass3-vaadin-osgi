package org.myftp.gattserver.grass3.pages;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.pages.template.OneColumnPage;
import org.myftp.gattserver.grass3.subwindows.InfoSubwindow;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
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

		LoginForm loginForm = new LoginForm() {

			private static final long serialVersionUID = -4417127436560572717L;

			@Override
			public String getLoginHTML() {
				String html = new String(super.getLoginHTML());

				// most browsers: do not use javascript to submit the form
				html = html
						.replace(
								"<form id='loginf' target='logintarget' onkeypress=\"submitOnEnter(event)\"",
								"<form id='loginf' target='logintarget'");

				// chrome: the form.action attribute cannot be set using
				// javascript - make it static
				String winPath = getRequest().getVaadinRequest().getPathInfo();
				html = html.replace("<form id='loginf'",
						"<form id='loginf' action='" + winPath
								+ "/loginHandler'");

				// chrome: the iframe.src attribute must not be blank
				html = html.replace("<iframe name='logintarget'",
						"<iframe name='logintarget' src='#'");

				// most browsers: use a "real" <input type=submit> element
				int buttonStartIdx = html.indexOf("<div><div onclick=");
				int buttonEndIdx = html.lastIndexOf("</form>") - 1;
				html = html.replace(
						html.substring(buttonStartIdx, buttonEndIdx),
						"<input type='submit' value='"
								+ getLoginButtonCaption() + "'");

				return html;
			}
		};
		loginForm.setUsernameCaption("Jméno");
		loginForm.setPasswordCaption("Heslo");
		loginForm.setLoginButtonCaption("Přihlásit");
		loginForm.addListener(new LoginForm.LoginListener() {

			private static final long serialVersionUID = -3342397991011184546L;

			public void onLogin(final LoginEvent event) {
				if (getGrassUI().login(event.getLoginParameter("username"),
						event.getLoginParameter("password"))) {

					InfoSubwindow infoSubwindow = new InfoSubwindow(
							"Přihlášení proběhlo úspěšně") {
						protected void onProceed(
								com.vaadin.ui.Button.ClickEvent event) {
							redirect(getPageURL(homePageFactory));
						};
					};
					getGrassUI().addWindow(infoSubwindow);

				} else {
					showError("Přihlášení se nezdařilo");
				}

			}
		});

		formLayout.addComponent(loginForm);
		formLayout.setSpacing(true);

		return layout;

	}
}
