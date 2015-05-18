package cz.gattserver.grass3.pg.service.impl;

import org.springframework.stereotype.Component;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.service.IContentService;

@Component("photogalleryContentService")
public class PhotogalleryContentService implements IContentService {

	public static final String ID = "cz.gattserver.grass3.pg:0.0.1";

	@javax.annotation.Resource(name = "photogalleryViewerPageFactory")
	private IPageFactory photogalleryViewerPageFactory;

	@javax.annotation.Resource(name = "photogalleryEditorPageFactory")
	private IPageFactory photogalleryEditorPageFactory;

	public String getCreateNewContentLabel() {
		return "Vytvořit novou galerii";
	}

	public com.vaadin.server.Resource getContentIcon() {
		return (com.vaadin.server.Resource) new ThemeResource("img/tags/img_16.png");
	}

	public String getContentID() {
		return ID;
	}

	public IPageFactory getContentEditorPageFactory() {
		return photogalleryEditorPageFactory;
	}

	public IPageFactory getContentViewerPageFactory() {
		return photogalleryViewerPageFactory;
	}

}
