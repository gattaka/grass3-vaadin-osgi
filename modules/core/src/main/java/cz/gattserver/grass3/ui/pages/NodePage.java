package cz.gattserver.grass3.ui.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.interfaces.NodeTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.ui.components.Breadcrumb;
import cz.gattserver.grass3.ui.components.ContentsLazyGrid;
import cz.gattserver.grass3.ui.components.ImageButton;
import cz.gattserver.grass3.ui.components.NewContentNodeGrid;
import cz.gattserver.grass3.ui.components.NodesGrid;
import cz.gattserver.grass3.ui.components.Breadcrumb.BreadcrumbElement;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.ui.ImageIcon;

public class NodePage extends OneColumnPage {

	@Autowired
	private ContentNodeService contentNodeFacade;

	// Přehled podkategorií
	private NodesGrid subNodesTable;

	public NodePage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createColumnContent() {

		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setPadding(true);

		VerticalLayout layout = new VerticalLayout();
		layout.setPadding(true);
		layout.setSpacing(true);
		marginLayout.add(layout);

		String nodeName = getRequest().getAnalyzer().getNextPathToken();
		if (nodeName == null)
			throw new GrassPageException(404);

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(nodeName);
		if (identifier == null)
			throw new GrassPageException(404);

		NodeTO node = nodeFacade.getNodeByIdForDetail(identifier.getId());

		// Navigační breadcrumb
		createBreadcrumb(layout, node);

		// Podkategorie
		createSubnodesPart(layout, node);

		// Obsahy
		createContentsPart(layout, node);

		return marginLayout;
	}

	private void createNewNodePanel(VerticalLayout layout, final NodeTO node) {

		HorizontalLayout panelLayout = new HorizontalLayout();
		panelLayout.setPadding(false);
		panelLayout.setSpacing(true);
		layout.add(panelLayout);

		final TextField newNodeNameField = new TextField();
		newNodeNameField.setPlaceholder("Nová kategorie");
		panelLayout.add(newNodeNameField);

		Button createButton = new ImageButton("Vytvořit novou kategorii", ImageIcon.BRIEFCASE_PLUS_16_ICON, e -> {
			String newNodeName = newNodeNameField.getValue();
			if (StringUtils.isBlank(newNodeName)) {
				UIUtils.showError("Název kategorie nesmí být prázdný");
				return;
			}
			Long newNodeId = nodeFacade.createNewNode(node.getId(), newNodeName);
			// refresh
			populateSubnodesTable(node);
			UIUtils.redirect(
					getPageURL(nodePageFactory, URLIdentifierUtils.createURLIdentifier(newNodeId, newNodeName)));
			// clean
			newNodeNameField.setValue("");
		});
		panelLayout.add(createButton);

	}

	private void createBreadcrumb(VerticalLayout layout, NodeTO node) {
		Breadcrumb breadcrumb = new Breadcrumb();
		layout.add(breadcrumb);

		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<>();
		NodeTO parent = node;
		while (true) {

			// nejprve zkus zjistit, zda předek existuje
			if (parent == null)
				throw new GrassPageException(404);

			breadcrumbElements.add(new BreadcrumbElement(parent.getName(), getPageURL(nodePageFactory,
					URLIdentifierUtils.createURLIdentifier(parent.getId(), parent.getName()))));

			// pokud je můj předek null, pak je to konec a je to všechno
			if (parent.getParent() == null)
				break;

			parent = parent.getParent();
		}

		breadcrumb.resetBreadcrumb(breadcrumbElements);
	}

	private void createSubnodesPart(VerticalLayout layout, NodeTO node) {
		VerticalLayout subNodesLayout = new VerticalLayout();
		subNodesLayout.setPadding(false);
		subNodesTable = new NodesGrid(NodePage.this);

		subNodesLayout.add(new H2("Podkategorie"));

		populateSubnodesTable(node);

		subNodesLayout.add(subNodesTable);
		layout.add(subNodesLayout);
		subNodesTable.setWidth("100%");

		// Vytvořit novou kategorii
		if (coreACL.canCreateNode(UIUtils.getUser())) {
			createNewNodePanel(subNodesLayout, node);
		}
	}

	private void populateSubnodesTable(NodeTO node) {
		List<NodeOverviewTO> nodes = nodeFacade.getNodesByParentNode(node.getId());
		if (nodes == null)
			throw new GrassPageException(500);
		subNodesTable.populate(nodes);
	}

	private void createContentsPart(VerticalLayout layout, NodeTO node) {
		VerticalLayout contentsLayout = new VerticalLayout();
		contentsLayout.setPadding(false);
		ContentsLazyGrid contentsTable = new ContentsLazyGrid();
		contentsTable.populate(this,
				q -> contentNodeFacade.getByNode(node.getId(), q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeFacade.getCountByNode(node.getId()));

		contentsLayout.add(new H2("Obsahy"));
		contentsLayout.add(contentsTable);
		contentsTable.setWidth("100%");
		layout.add(contentsLayout);

		// Vytvořit obsahy
		createNewContentMenu(layout, node);
	}

	private void createNewContentMenu(VerticalLayout layout, NodeTO node) {
		VerticalLayout newContentsLayout = new VerticalLayout();
		newContentsLayout.setPadding(false);
		NewContentNodeGrid newContentsTable = new NewContentNodeGrid(NodePage.this, node);

		newContentsLayout.add(new H2("Vytvořit nový obsah"));
		newContentsLayout.add(newContentsTable);
		newContentsTable.setWidth("100%");
		newContentsLayout.setVisible(coreACL.canCreateContent(UIUtils.getUser()));

		layout.add(newContentsLayout);
	}

}
