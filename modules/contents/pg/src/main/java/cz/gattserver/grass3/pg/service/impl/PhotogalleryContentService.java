package cz.gattserver.grass3.pg.service.impl;

import org.springframework.stereotype.Component;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.modules.ContentService;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.web.common.ui.ImageIcons;

@Component("photogalleryContentService")
public class PhotogalleryContentService implements ContentService {

	public static final String ID = "cz.gattserver.grass3.pg:0.0.1";

	@javax.annotation.Resource(name = "photogalleryViewerPageFactory")
	private PageFactory photogalleryViewerPageFactory;

	@javax.annotation.Resource(name = "photogalleryEditorPageFactory")
	private PageFactory photogalleryEditorPageFactory;

	public String getCreateNewContentLabel() {
		return "Vytvořit novou galerii";
	}

	public ThemeResource getContentIcon() {
		return new ThemeResource(ImageIcons.IMG_16_ICON);
	}

	public String getContentID() {
		return ID;
	}

	public PageFactory getContentEditorPageFactory() {
		return photogalleryEditorPageFactory;
	}

	public PageFactory getContentViewerPageFactory() {
		return photogalleryViewerPageFactory;
	}

}
