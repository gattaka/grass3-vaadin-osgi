package cz.gattserver.grass3.pg.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pg.pages.PhotogalleryEditorPage;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("photogalleryEditorPageFactory")
public class PhotogalleryEditorPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 4542633366840601783L;

	public PhotogalleryEditorPageFactory() {
		super("photogallery-editor");
	}

	@Override
	protected boolean isAuthorized() {
		return getUser() != null
				&& (getUser().isAdmin() || getUser().hasRole(Role.AUTHOR));
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new PhotogalleryEditorPage(request);
	}

}
