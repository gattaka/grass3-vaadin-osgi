package cz.gattserver.grass3.ui.components;

import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;

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

		addColumn(new IconRenderer<NodeOverviewTO>(c -> new Image(ImageIcon.BRIEFCASE_16_ICON.createResource(), "")))
				.setHeader("").setKey(iconBind).setClassNameGenerator(item -> "icon-cell");

		addColumn(new ComponentRenderer<Anchor, NodeOverviewTO>(node -> {
			String url = page.getPageURL(nodePageFactory,
					URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName()));
			return new Anchor(url, node.getName());
		})).setHeader("Kategorie").setId(nameBind);

		setColumns(iconBind, nameBind);

	}

	public void populate(List<NodeOverviewTO> nodes) {
		setItems(nodes);
		setHeight(GridUtils.processHeight(nodes.size()) + "px");
	}

}