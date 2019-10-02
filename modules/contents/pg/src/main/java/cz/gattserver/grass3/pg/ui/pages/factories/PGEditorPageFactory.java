package cz.gattserver.grass3.pg.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pg.ui.pages.PGEditorPage;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("pgEditorPageFactory")
public class PGEditorPageFactory extends AbstractPageFactory {

	public PGEditorPageFactory() {
		super("pg-editor");
	}

	@Override
	protected boolean isAuthorized() {
		return getUser() != null && (getUser().isAdmin() || getUser().hasRole(CoreRole.AUTHOR));
	}

	@Override
	protected GrassPage createPage() {
		return new PGEditorPage();
	}

}
