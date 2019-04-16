package cz.gattserver.grass3.ui.components;

import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Image;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.MenuPage;
import cz.gattserver.grass3.ui.util.GridUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;

public class NodesGrid extends Grid<NodeOverviewTO> {

	private static final long serialVersionUID = -2220485504407844582L;

	public NodesGrid(MenuPage page) {
		// inject nefunguje kvůli něčemu v předkovi
		final PageFactory nodePageFactory = (PageFactory) SpringContextHelper.getBean("nodePageFactory");

		setHeight("200px");
		setSelectionMode(SelectionMode.NONE);

		String iconBind = "customIcon";
		String nameBind = "customName";

		addColumn(contentNode -> new Image("", ImageIcon.BRIEFCASE_16_ICON.createResource()), new ComponentRenderer())
				.setWidth(GridUtils.ICON_COLUMN_WIDTH).setCaption("").setId(iconBind);

		addColumn(node -> "<a href='"
				+ page.getPageURL(nodePageFactory, URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName()))
				+ "'>" + node.getName() + "</a>", new HtmlRenderer()).setCaption("Kategorie").setId(nameBind);

		setColumns(iconBind, nameBind);

	}

	public void populate(List<NodeOverviewTO> nodes) {
		setItems(nodes);
		setHeight(GridUtils.processHeight(nodes.size()) + "px");
	}

}