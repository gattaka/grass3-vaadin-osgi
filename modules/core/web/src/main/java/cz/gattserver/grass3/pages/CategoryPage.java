package cz.gattserver.grass3.pages;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import cz.gattserver.grass3.facades.IContentNodeFacade;
import cz.gattserver.grass3.facades.INodeFacade;
import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;
import cz.gattserver.grass3.model.dto.NodeBreadcrumbDTO;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.pages.template.ContentsTableFactory;
import cz.gattserver.grass3.pages.template.NewContentsTableFactory;
import cz.gattserver.grass3.pages.template.NodesTableFactory;
import cz.gattserver.grass3.pages.template.OneColumnPage;
import cz.gattserver.grass3.pages.template.ContentsTableFactory.ContentsTable;
import cz.gattserver.grass3.pages.template.NewContentsTableFactory.NewContentsTable;
import cz.gattserver.grass3.pages.template.NodesTableFactory.NodesTable;
import cz.gattserver.grass3.security.ICoreACL;
import cz.gattserver.grass3.template.Breadcrumb;
import cz.gattserver.grass3.template.Breadcrumb.BreadcrumbElement;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.URLIdentifierUtils;

public class CategoryPage extends OneColumnPage {

	private static final long serialVersionUID = -499585200973560016L;

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	@Resource(name = "nodeFacade")
	private INodeFacade nodeFacade;

	@Resource(name = "contentNodeFacade")
	private IContentNodeFacade contentNodeFacade;

	@Resource(name = "categoryPageFactory")
	private IPageFactory categoryPageFactory;

	@Resource(name = "contentsTableFactory")
	private ContentsTableFactory contentsTableFactory;

	@Resource(name = "newContentsTableFactory")
	private NewContentsTableFactory newContentsTableFactory;

	@Resource(name = "nodesTableFactory")
	private NodesTableFactory nodesTableFactory;

	// Přehled podkategorií
	private NodesTable subNodesTable;
	private Label noSubNodesLabel;

	public CategoryPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void init() {
		noSubNodesLabel = new Label("Nebyly nalezeny žádné podkategorie");
		super.init();
	}

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();

		String categoryName = getRequest().getAnalyzer().getNextPathToken();
		if (categoryName == null)
			showError404();

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(categoryName);
		if (identifier == null) {
			showError404();
			return layout;
		}

		NodeDTO node = nodeFacade.getNodeByIdForDetail(identifier.getId());

		layout.setMargin(true);
		layout.setSpacing(true);

		// TODO pokud má jiný název, přesměruj na kategorii s ID-Název správným
		// názvem

		// Navigační breadcrumb
		createBreadcrumb(layout, node);

		// Podkategorie
		createSubnodesPart(layout, node);

		// Obsahy
		createContentsPart(layout, node);

		return layout;
	}

	private void createNewNodePanel(VerticalLayout layout, final NodeDTO node) {

		Panel panel = new Panel("Vytvořit novou kategorii");
		layout.addComponent(panel);

		HorizontalLayout panelBackgroudLayout = new HorizontalLayout();
		panelBackgroudLayout.setSizeFull();
		panel.setContent(panelBackgroudLayout);

		HorizontalLayout panelLayout = new HorizontalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setMargin(true);
		panelBackgroudLayout.addComponent(panelLayout);

		final TextField newNodeName = new TextField();
		// newNodeName.setWidth("200px");
		newNodeName.setRequired(true);
		newNodeName.setRequiredError("Název kategorie nesmí být prázdný");
		panelLayout.addComponent(newNodeName);

		Button createButton = new Button("Vytvořit", new Button.ClickListener() {

			private static final long serialVersionUID = -4315617904120991885L;

			public void buttonClick(ClickEvent event) {
				if (newNodeName.isValid() == false)
					return;

				if (nodeFacade.createNewNode(node, newNodeName.getValue().toString())) {
					showInfo("Nový kategorie byla úspěšně vytvořena.");
					// refresh
					populateSubnodesTable(node);
					subNodesTable.setVisible(true);
					noSubNodesLabel.setVisible(false);
					// clean
					newNodeName.setValue("");
				} else {
					showWarning("Nezdařilo se vložit novou kategorii.");
				}
			}
		});
		panelLayout.addComponent(createButton);

	}

	private void createBreadcrumb(VerticalLayout layout, NodeBreadcrumbDTO node) {

		Breadcrumb breadcrumb = new Breadcrumb();
		layout.addComponent(breadcrumb);

		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<BreadcrumbElement>();
		NodeBreadcrumbDTO parent = node;
		while (true) {

			// nejprve zkus zjistit, zda předek existuje
			if (parent == null)
				showError404();

			breadcrumbElements.add(new BreadcrumbElement(parent.getName(), getPageResource(categoryPageFactory,
					URLIdentifierUtils.createURLIdentifier(parent.getId(), parent.getName()))));

			// pokud je můj předek null, pak je to konec a je to všechno
			if (parent.getParent() == null)
				break;

			parent = parent.getParent();
		}

		breadcrumb.resetBreadcrumb(breadcrumbElements);
	}

	private void createSubnodesPart(VerticalLayout layout, NodeDTO node) {

		VerticalLayout subNodesLayout = new VerticalLayout();
		subNodesTable = nodesTableFactory.createNodesTable();

		subNodesLayout.addComponent(new Label("<h2>Podkategorie</h2>", ContentMode.HTML));

		populateSubnodesTable(node);

		subNodesLayout.addComponent(subNodesTable);
		layout.addComponent(subNodesLayout);
		subNodesTable.setWidth("100%");

		noSubNodesLabel.setWidth(null);
		subNodesLayout.addComponent(noSubNodesLabel);
		subNodesLayout.setComponentAlignment(noSubNodesLabel, Alignment.MIDDLE_CENTER);

		// Vytvořit novou kategorii
		if (coreACL.canCreateCategory(getUser())) {
			createNewNodePanel(layout, node);
		}
	}

	private void populateSubnodesTable(NodeDTO node) {

		List<NodeDTO> nodes = nodeFacade.getNodesByParentNode(node);
		if (nodes == null) {
			showError500();
			return;
		}
		subNodesTable.populateTable(nodes, this);

		subNodesTable.setVisible(nodes.size() != 0);
		noSubNodesLabel.setVisible(nodes.size() == 0);

		int min = 50;
		int element = 25;
		int max = 200;
		int header = 25;

		int size = nodes.size() * element;

		if (size < min)
			size = min;
		if (size > max)
			size = max;
		size += header;
		subNodesTable.setHeight(size + "px");
	}

	private void createContentsPart(VerticalLayout layout, NodeDTO node) {

		VerticalLayout contentsLayout = new VerticalLayout();
		ContentsTable contentsTable = contentsTableFactory.createContentsTableWithoutCategoryColumn();

		List<ContentNodeOverviewDTO> contentNodes = node.getContentNodes();
		if (contentNodes == null)
			showError500();

		contentsLayout.addComponent(new Label("<h2>Obsahy</h2>", ContentMode.HTML));
		contentsLayout.addComponent(contentsTable);
		contentsTable.setWidth("100%");
		layout.addComponent(contentsLayout);

		int min = 50;
		int element = 25;
		int max = 400;
		int header = 25;

		int size = contentsTable.populateTable(contentNodes, this) * element;

		if (size < min)
			size = min;
		if (size > max)
			size = max;
		size += header;
		contentsTable.setHeight(size + "px");

		// Vytvořit obsahy
		createNewContentMenu(layout, node);

	}

	private void createNewContentMenu(VerticalLayout layout, NodeDTO node) {

		VerticalLayout newContentsLayout = new VerticalLayout();
		NewContentsTable newContentsTable = newContentsTableFactory.createNewContentsTable();

		newContentsLayout.addComponent(new Label("<h2>Vytvořit nový obsah</h2>", ContentMode.HTML));
		newContentsLayout.addComponent(newContentsTable);
		newContentsTable.setWidth("100%");
		newContentsTable.setHeight("100px");

		if (coreACL.canCreateContent(getUser())) {
			newContentsLayout.setVisible(true);
			newContentsTable.populateTable(node, this);
		} else {
			newContentsLayout.setVisible(false);
		}

		layout.addComponent(newContentsLayout);
	}

}
