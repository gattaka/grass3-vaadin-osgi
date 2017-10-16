package cz.gattserver.grass3.pages.factories;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletResponse;

import cz.gattserver.grass3.pages.LoginPage;
import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("logoutPageFactory")
public class LogoutPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 259563676017857878L;

	public LogoutPageFactory() {
		super("logoutpage");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			Page.getCurrent().reload();
			new SecurityContextLogoutHandler().logout(VaadinServletRequest.getCurrent(),
					VaadinServletResponse.getCurrent(), auth);
			SecurityContextHolder.clearContext();
		}

		return new LoginPage(request);
	}

}
