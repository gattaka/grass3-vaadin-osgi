package cz.gattserver.grass3.pg.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pg.ui.pages.PGEditorPage;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("pgEditorPageFactory")
public class PGEditorPageFactory extends AbstractPageFactory {

	public PGEditorPageFactory() {
		super("pg-editor");
	}

	@Override
	protected boolean isAuthorized() {
		return getUser() != null && (getUser().isAdmin() || getUser().hasRole(Role.AUTHOR));
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new PGEditorPage(request);
	}

}
