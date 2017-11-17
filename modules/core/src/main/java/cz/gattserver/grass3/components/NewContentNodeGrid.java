package cz.gattserver.grass3.components;

import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.ImageRenderer;

import cz.gattserver.grass3.interfaces.NodeTO;
import cz.gattserver.grass3.pages.template.MenuPage;
import cz.gattserver.grass3.register.ServiceRegister;
import cz.gattserver.grass3.service.ContentService;
import cz.gattserver.grass3.ui.util.GridUtils;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.URLIdentifierUtils;

public class NewContentNodeGrid extends Grid<ContentService> {

	private static final long serialVersionUID = -2220485504407844582L;

	public NewContentNodeGrid(MenuPage page, final NodeTO node) {
		// inject nefunguje kvůli něčemu v předkovi
		final ServiceRegister serviceHolder = (ServiceRegister) SpringContextHelper.getContext()
				.getBean(ServiceRegister.class);

		setSelectionMode(SelectionMode.NONE);

		String iconBind = "customIcon";
		String nameBind = "customName";

		// jaké služby obsahů mám k dispozici ?
		List<ContentService> contentServices = serviceHolder.getContentServices();
		setItems(contentServices);

		addColumn(contentService -> contentService.getContentIcon(), new ImageRenderer<>())
				.setWidth(GridUtils.ICON_COLUMN_WIDTH).setCaption("").setId(iconBind);

		addColumn(contentService -> {
			return "<a href='"
					+ page.getPageURL(contentService.getContentEditorPageFactory(),
							DefaultContentOperations.NEW.toString(),
							URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName()))
					+ "'>" + contentService.getCreateNewContentLabel() + "</a>";
		}, new HtmlRenderer()).setCaption("Obsah").setId(nameBind);

		setColumns(iconBind, nameBind);

		setHeight(GridUtils.processHeight(contentServices.size()) + "px");

	}

}