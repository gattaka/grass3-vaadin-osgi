package org.myftp.gattserver.grass3.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.IContentNodeFacade;
import org.myftp.gattserver.grass3.facades.INodeFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.pages.template.ContentsTableFactory;
import org.myftp.gattserver.grass3.pages.template.NewContentsTableFactory;
import org.myftp.gattserver.grass3.pages.template.ContentsTableFactory.ContentsTable;
import org.myftp.gattserver.grass3.pages.template.NewContentsTableFactory.NewContentsTable;
import org.myftp.gattserver.grass3.pages.template.NodesTableFactory;
import org.myftp.gattserver.grass3.pages.template.NodesTableFactory.NodesTable;
import org.myftp.gattserver.grass3.pages.template.OneColumnPage;
import org.myftp.gattserver.grass3.security.ICoreACL;
import org.myftp.gattserver.grass3.template.Breadcrumb;
import org.myftp.gattserver.grass3.template.Breadcrumb.BreadcrumbElement;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.springframework.context.annotation.Scope;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component("categoryPage")
@Scope("prototype")
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

	public CategoryPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();

		String categoryName = getRequest().getAnalyzer().getPathToken(1);
		if (categoryName == null)
			showError404();

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
				.parseURLIdentifier(categoryName);
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
		createSubnodesTable(layout, node);

		// Obsahy
		createContent(layout, node);

		// Vytvořit obsahy
		createNewContentMenu(layout, node);

		return layout;
	}

	private void createBreadcrumb(VerticalLayout layout, NodeDTO node) {

		Breadcrumb breadcrumb = new Breadcrumb();
		layout.addComponent(breadcrumb);

		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<BreadcrumbElement>();
		NodeDTO parent = node;
		while (true) {

			// nejprve zkus zjistit, zda předek existuje
			if (parent == null)
				showError404();

			breadcrumbElements.add(new BreadcrumbElement(parent.getName(),
					getPageResource(
							categoryPageFactory,
							URLIdentifierUtils.createURLIdentifier(
									parent.getId(), parent.getName()))));

			// pokud je můj předek null, pak je to konec a je to všechno
			if (parent.getParent() == null)
				break;

			parent = parent.getParent();
		}

		breadcrumb.resetBreadcrumb(breadcrumbElements);
	}

	private void createSubnodesTable(VerticalLayout layout, NodeDTO node) {

		VerticalLayout subNodesLayout = new VerticalLayout();
		NodesTable subNodesTable = nodesTableFactory.createNodesTable();

		subNodesLayout.addComponent(new Label("<h2>Podkategorie</h2>",
				ContentMode.HTML));
		subNodesLayout.addComponent(subNodesTable);
		layout.addComponent(subNodesLayout);
		subNodesTable.setWidth("100%");

		List<NodeDTO> nodes = node.getSubNodes();
		if (nodes == null)
			showError500();

		subNodesTable.populateTable(nodes, this);
	}

	private void createContent(VerticalLayout layout, NodeDTO node) {

		VerticalLayout contentsLayout = new VerticalLayout();
		ContentsTable contentsTable = contentsTableFactory
				.createContentsTableWithoutCategoryColumn();

		Set<ContentNodeDTO> contentNodes = node.getContentNodes();
		if (contentNodes == null)
			showError500();

		contentsLayout.addComponent(new Label("<h2>Obsahy</h2>",
				ContentMode.HTML));
		contentsLayout.addComponent(contentsTable);
		contentsTable.setWidth("100%");
		layout.addComponent(contentsLayout);

		contentsTable.populateTable(contentNodes, this);

	}

	private void createNewContentMenu(VerticalLayout layout, NodeDTO node) {

		VerticalLayout newContentsLayout = new VerticalLayout();
		NewContentsTable newContentsTable = newContentsTableFactory
				.createNewContentsTable();

		newContentsLayout.addComponent(new Label(
				"<h2>Vytvořit nový obsah</h2>", ContentMode.HTML));
		newContentsLayout.addComponent(newContentsTable);
		newContentsTable.setWidth("100%");

		if (coreACL.canCreateContent(getUser())) {
			newContentsLayout.setVisible(true);
			newContentsTable.populateTable(node, this);
		} else {
			newContentsLayout.setVisible(false);
		}

		layout.addComponent(newContentsLayout);
	}

}
