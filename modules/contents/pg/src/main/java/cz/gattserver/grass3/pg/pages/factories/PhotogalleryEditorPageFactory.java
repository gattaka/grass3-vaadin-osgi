package cz.gattserver.grass3.pg.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.IGrassPage;
import cz.gattserver.grass3.pg.pages.PhotogalleryEditorPage;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("photogalleryEditorPageFactory")
public class PhotogalleryEditorPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 4542633366840601783L;

	public PhotogalleryEditorPageFactory() {
		super("photogallery-editor");
	}

	@Override
	protected boolean isAuthorized() {
		return getUser() != null
				&& (getUser().getRoles().contains(Role.ADMIN) || getUser().getRoles().contains(Role.AUTHOR));
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new PhotogalleryEditorPage(request);
	}

}
