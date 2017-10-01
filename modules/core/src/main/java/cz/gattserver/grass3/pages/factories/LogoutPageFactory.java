package cz.gattserver.grass3.pages.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;

import cz.gattserver.grass3.pages.LoginPage;
import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("logoutPageFactory")
public class LogoutPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 259563676017857878L;

	@Autowired
	private CoreACL coreACL;

	public LogoutPageFactory() {
		super("logoutpage");
	}

	@Override
	protected boolean isAuthorized() {
		// lze pouze je-li přihlášen
		return coreACL.isLoggedIn(getUser());
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(null);
		SecurityContextHolder.clearContext();
		VaadinSession.getCurrent().close();
		Page.getCurrent().reload();
		
		return new LoginPage(request);
	}

}
