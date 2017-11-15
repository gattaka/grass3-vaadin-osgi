package cz.gattserver.grass3.components;

import java.util.List;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.ImageRenderer;

import cz.gattserver.grass3.model.dto.NodeOverviewDTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.pages.template.MenuPage;
import cz.gattserver.grass3.ui.util.GridUtils;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.ui.ImageIcons;

public class NodesGrid extends Grid<NodeOverviewDTO> {

	private static final long serialVersionUID = -2220485504407844582L;

	public NodesGrid(MenuPage page) {
		// inject nefunguje kvůli něčemu v předkovi
		final PageFactory nodePageFactory = (PageFactory) SpringContextHelper.getBean("nodePageFactory");

		setHeight("200px");
		setSelectionMode(SelectionMode.NONE);

		String iconBind = "customIcon";
		String nameBind = "customName";

		addColumn(contentNode -> new ThemeResource(ImageIcons.BRIEFCASE_16_ICON), new ImageRenderer<>())
				.setWidth(GridUtils.ICON_COLUMN_WIDTH).setCaption("").setId(iconBind);

		addColumn(node -> {
			return "<a href='"
					+ page.getPageURL(nodePageFactory,
							URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName()))
					+ "'>" + node.getName() + "</a>";
		}, new HtmlRenderer()).setCaption("Kategorie").setId(nameBind);

		setColumns(iconBind, nameBind);

	}

	public void populate(List<NodeOverviewDTO> nodes) {
		setItems(nodes);
		setHeight(GridUtils.processHeight(nodes.size()) + "px");
	}

}