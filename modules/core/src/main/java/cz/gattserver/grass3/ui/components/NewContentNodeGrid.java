package cz.gattserver.grass3.ui.components;

import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Image;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import cz.gattserver.grass3.interfaces.NodeTO;
import cz.gattserver.grass3.modules.ContentModule;
import cz.gattserver.grass3.modules.register.ModuleRegister;
import cz.gattserver.grass3.ui.pages.template.MenuPage;
import cz.gattserver.grass3.ui.util.GridUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;

public class NewContentNodeGrid extends Grid<ContentModule> {

	private static final long serialVersionUID = -2220485504407844582L;

	public NewContentNodeGrid(MenuPage page, final NodeTO node) {
		// inject nefunguje kvůli něčemu v předkovi
		final ModuleRegister serviceHolder = (ModuleRegister) SpringContextHelper.getContext()
				.getBean(ModuleRegister.class);

		setSelectionMode(SelectionMode.NONE);

		String iconBind = "customIcon";
		String nameBind = "customName";

		// jaké služby obsahů mám k dispozici ?
		List<ContentModule> contentServices = serviceHolder.getContentServices();
		setItems(contentServices);

		addColumn(contentService -> new Image("", contentService.getContentIcon()), new ComponentRenderer())
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