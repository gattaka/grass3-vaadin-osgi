package cz.gattserver.grass3.pages;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.components.Breadcrumb;
import cz.gattserver.grass3.components.ContentsLazyGrid;
import cz.gattserver.grass3.components.NewContentNodeGrid;
import cz.gattserver.grass3.components.NodesGrid;
import cz.gattserver.grass3.components.Breadcrumb.BreadcrumbElement;
import cz.gattserver.grass3.facades.ContentNodeFacade;
import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.interfaces.NodeTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.pages.template.OneColumnPage;
import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.ui.ImageIcons;

public class NodePage extends OneColumnPage {

	@Autowired
	private CoreACL coreACL;

	@Autowired
	private NodeFacade nodeFacade;

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	@Resource(name = "nodePageFactory")
	private PageFactory nodePageFactory;

	// Přehled podkategorií
	private NodesGrid subNodesTable;

	public NodePage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setMargin(true);

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		marginLayout.addComponent(layout);

		String nodeName = getRequest().getAnalyzer().getNextPathToken();
		if (nodeName == null)
			UIUtils.showErrorPage404();

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(nodeName);
		if (identifier == null) {
			UIUtils.showErrorPage404();
			return layout;
		}

		NodeTO node = nodeFacade.getNodeByIdForDetail(identifier.getId());

		// TODO pokud má jiný název, přesměruj na kategorii s ID-Název správným
		// názvem

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
		panelLayout.setMargin(false);
		panelLayout.setSpacing(true);
		layout.addComponent(panelLayout);

		final TextField newNodeNameField = new TextField();
		newNodeNameField.setPlaceholder("Nová kategorie");
		panelLayout.addComponent(newNodeNameField);

		Button createButton = new Button("Vytvořit novou kategorii", e -> {
			String newNodeName = newNodeNameField.getValue();
			if (StringUtils.isBlank(newNodeName)) {
				UIUtils.showError("Název kategorie nesmí být prázdný");
				return;
			}
			Long newNodeId = nodeFacade.createNewNode(node.getId(), newNodeName);
			if (newNodeId != null) {
				UIUtils.showInfo("Nový kategorie byla úspěšně vytvořena.");
				// refresh
				populateSubnodesTable(node);
				UIUtils.redirect(
						getPageURL(nodePageFactory, URLIdentifierUtils.createURLIdentifier(newNodeId, newNodeName)));
				// clean
				newNodeNameField.setValue("");
			} else {
				UIUtils.showWarning("Nezdařilo se vložit novou kategorii.");
			}
		});
		createButton.setIcon(new ThemeResource(ImageIcons.BRIEFCASE_PLUS_16_ICON));
		panelLayout.addComponent(createButton);

	}

	private void createBreadcrumb(VerticalLayout layout, NodeTO node) {

		Breadcrumb breadcrumb = new Breadcrumb();
		layout.addComponent(breadcrumb);

		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<BreadcrumbElement>();
		NodeTO parent = node;
		while (true) {

			// nejprve zkus zjistit, zda předek existuje
			if (parent == null)
				UIUtils.showErrorPage404();

			breadcrumbElements.add(new BreadcrumbElement(parent.getName(), getPageResource(nodePageFactory,
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
		subNodesLayout.setMargin(false);
		subNodesTable = new NodesGrid(NodePage.this);

		subNodesLayout.addComponent(new H2Label("Podkategorie"));

		populateSubnodesTable(node);

		subNodesLayout.addComponent(subNodesTable);
		layout.addComponent(subNodesLayout);
		subNodesTable.setWidth("100%");

		// Vytvořit novou kategorii
		if (coreACL.canCreateNode(UIUtils.getUser())) {
			createNewNodePanel(subNodesLayout, node);
		}
	}

	private void populateSubnodesTable(NodeTO node) {
		List<NodeOverviewTO> nodes = nodeFacade.getNodesByParentNode(node.getId());
		if (nodes == null) {
			UIUtils.showErrorPage500();
			return;
		}
		subNodesTable.populate(nodes);
	}

	private void createContentsPart(VerticalLayout layout, NodeTO node) {
		VerticalLayout contentsLayout = new VerticalLayout();
		contentsLayout.setMargin(false);
		ContentsLazyGrid contentsTable = new ContentsLazyGrid();
		contentsTable.populate(this, (sortOrder, offset, limit) -> {
			return contentNodeFacade.getByNode(node.getId(), offset / limit, limit).stream();
		}, () -> {
			return contentNodeFacade.getCountByNode(node.getId());
		});

		contentsLayout.addComponent(new H2Label("Obsahy"));
		contentsLayout.addComponent(contentsTable);
		contentsTable.setWidth("100%");
		layout.addComponent(contentsLayout);

		// Vytvořit obsahy
		createNewContentMenu(layout, node);
	}

	private void createNewContentMenu(VerticalLayout layout, NodeTO node) {
		VerticalLayout newContentsLayout = new VerticalLayout();
		newContentsLayout.setMargin(false);
		NewContentNodeGrid newContentsTable = new NewContentNodeGrid(NodePage.this, node);

		newContentsLayout.addComponent(new H2Label("Vytvořit nový obsah"));
		newContentsLayout.addComponent(newContentsTable);
		newContentsTable.setWidth("100%");
		newContentsLayout.setVisible(coreACL.canCreateContent(UIUtils.getUser()));

		layout.addComponent(newContentsLayout);
	}

}
