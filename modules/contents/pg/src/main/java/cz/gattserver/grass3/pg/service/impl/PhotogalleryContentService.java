package cz.gattserver.grass3.pg.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.ContentModule;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.web.common.ui.ImageIcon;

@Component("photogalleryContentService")
public class PhotogalleryContentService implements ContentModule {

	public static final String ID = "cz.gattserver.grass3.pg:0.0.1";

	@Resource(name = "photogalleryViewerPageFactory")
	private PageFactory photogalleryViewerPageFactory;

	@Resource(name = "photogalleryEditorPageFactory")
	private PageFactory photogalleryEditorPageFactory;

	@Override
	public String getCreateNewContentLabel() {
		return "Vytvořit novou galerii";
	}

	@Override
	public com.vaadin.server.Resource getContentIcon() {
		return ImageIcon.IMG_16_ICON.createResource();
	}

	@Override
	public String getContentID() {
		return ID;
	}

	@Override
	public PageFactory getContentEditorPageFactory() {
		return photogalleryEditorPageFactory;
	}

	@Override
	public PageFactory getContentViewerPageFactory() {
		return photogalleryViewerPageFactory;
	}

}
