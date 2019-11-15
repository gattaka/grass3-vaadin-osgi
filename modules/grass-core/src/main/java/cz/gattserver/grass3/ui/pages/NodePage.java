package cz.gattserver.grass3.ui.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.interfaces.NodeTO;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.ui.components.Breadcrumb;
import cz.gattserver.grass3.ui.components.ContentsLazyGrid;
import cz.gattserver.grass3.ui.components.NewContentNodeGrid;
import cz.gattserver.grass3.ui.components.NodesGrid;
import cz.gattserver.grass3.ui.components.SaveCloseLayout;
import cz.gattserver.grass3.ui.components.Breadcrumb.BreadcrumbElement;
import cz.gattserver.grass3.ui.components.button.ImageButton;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.WebDialog;

@Route("category")
public class NodePage extends OneColumnPage implements HasUrlParameter<String> {

	private static final long serialVersionUID = 1560125362904332256L;

	@Autowired
	private ContentNodeService contentNodeFacade;

	// Přehled podkategorií
	private NodesGrid subNodesTable;

	private String categoryParameter;

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		categoryParameter = parameter;
		init();
	}

	@Override
	protected void createColumnContent(Div layout) {
		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(categoryParameter);
		if (identifier == null)
			throw new GrassPageException(404);

		NodeTO node = nodeFacade.getNodeByIdForDetail(identifier.getId());

		// Navigační breadcrumb
		createBreadcrumb(layout, node);

		// Podkategorie
		createSubnodesPart(layout, node);

		// Obsahy
		createContentsPart(layout, node);
	}

	private void createNewNodePanel(Div layout, final NodeTO node) {
		ButtonLayout buttonLayout = new ButtonLayout();
		layout.add(buttonLayout);
		Button createButton = new ImageButton("Vytvořit novou kategorii", ImageIcon.BRIEFCASE_PLUS_16_ICON,
				e -> createNodeAction(node));
		buttonLayout.add(createButton);
	}

	public void createNodeAction(NodeOverviewTO parentNode) {
		final WebDialog dialog = new WebDialog();

		final TextField newNameField = new TextField();
		newNameField.setPlaceholder("Nová kategorie do " + parentNode.getName());
		newNameField.setWidthFull();
		dialog.addComponent(newNameField);

		NodeOverviewTO to = new NodeOverviewTO();
		Binder<NodeOverviewTO> binder = new Binder<>(NodeOverviewTO.class);
		binder.forField(newNameField).withValidator(StringUtils::isNotBlank, "Název kategorie nesmí být prázdný")
				.bind(NodeOverviewTO::getName, NodeOverviewTO::setName);
		binder.setBean(to);

		HorizontalLayout btnLayout = new HorizontalLayout();
		dialog.addComponent(btnLayout);

		SaveCloseLayout saveCloseLayout = new SaveCloseLayout(event -> {
			if (binder.validate().isOk()) {
				Long newNodeId = nodeFacade.createNewNode(parentNode.getId(), to.getName());
				UIUtils.redirect(
						getPageURL(nodePageFactory, URLIdentifierUtils.createURLIdentifier(newNodeId, to.getName())));
				dialog.close();
			}
		}, event -> dialog.close());
		dialog.add(saveCloseLayout);

		dialog.setWidth("200px");

		dialog.open();
	}

	private void createBreadcrumb(Div layout, NodeTO node) {
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

	private void createSubnodesPart(Div layout, NodeTO node) {
		subNodesTable = new NodesGrid(NodePage.this);

		layout.add(new H2("Podkategorie"));

		populateSubnodesTable(node);

		layout.add(subNodesTable);
		subNodesTable.setWidthFull();

		// Vytvořit novou kategorii
		if (coreACL.canCreateNode(getUser()))
			createNewNodePanel(layout, node);
	}

	private void populateSubnodesTable(NodeTO node) {
		List<NodeOverviewTO> nodes = nodeFacade.getNodesByParentNode(node.getId());
		if (nodes == null)
			throw new GrassPageException(500);
		subNodesTable.populate(nodes);
	}

	private void createContentsPart(Div layout, NodeTO node) {
		layout.add(new H2("Obsahy"));

		ContentsLazyGrid contentsTable = new ContentsLazyGrid();
		contentsTable.populate(getUser().getId() != null, this,
				q -> contentNodeFacade.getByNode(node.getId(), q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeFacade.getCountByNode(node.getId()));
		layout.add(contentsTable);
		contentsTable.setWidthFull();

		// Vytvořit obsahy
		if (coreACL.canCreateContent(getUser()))
			createNewContentMenu(layout, node);
	}

	private void createNewContentMenu(Div layout, NodeTO node) {
		layout.add(new H2("Vytvořit nový obsah"));
		NewContentNodeGrid newContentsTable = new NewContentNodeGrid(NodePage.this, node);
		layout.add(newContentsTable);
		newContentsTable.setWidthFull();
	}

}
