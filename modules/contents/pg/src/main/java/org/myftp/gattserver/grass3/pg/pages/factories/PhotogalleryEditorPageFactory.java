package org.myftp.gattserver.grass3.pg.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.springframework.stereotype.Component;

@Component("photogalleryEditorPageFactory")
public class PhotogalleryEditorPageFactory extends AbstractPageFactory {

	public PhotogalleryEditorPageFactory() {
		super("photogallery-editor", "photogalleryEditorPage");
	}

	@Override
	protected boolean isAuthorized() {
		return getUser().getRoles().contains(Role.ADMIN)
				|| getUser().getRoles().contains(Role.AUTHOR);
	}

}
