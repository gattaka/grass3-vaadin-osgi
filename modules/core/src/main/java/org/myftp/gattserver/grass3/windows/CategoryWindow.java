package org.myftp.gattserver.grass3.windows;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.facades.ContentNodeFacade;
import org.myftp.gattserver.grass3.facades.NodeFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.template.Breadcrumb;
import org.myftp.gattserver.grass3.template.Breadcrumb.BreadcrumbElement;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.windows.template.ContentsTable;
import org.myftp.gattserver.grass3.windows.template.NewContentsTable;
import org.myftp.gattserver.grass3.windows.template.NodesTable;
import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class CategoryWindow extends OneColumnWindow {

	private static final long serialVersionUID = -499585200973560016L;

	private NodeFacade nodeFacade = NodeFacade.INSTANCE;
	private ContentNodeFacade contentNodeFacade = ContentNodeFacade.INSTANCE;

	private final ContentsTable contentsTable = new ContentsTable();
	private final NodesTable subNodesTable = new NodesTable();
	private final NewContentsTable newContentsTable = new NewContentsTable();

	public static final String NAME = "category";

	public CategoryWindow() {
		setName(NAME);
		setCaption("Gattserver");
	}

	/**
	 * Breadcrumb
	 */
	private Breadcrumb breadcrumb;

	@Override
	protected void createContent(VerticalLayout layout) {
		layout.addComponent(breadcrumb = new Breadcrumb());

		layout.setMargin(true);
		layout.setSpacing(true);

		// Podkategorie
		VerticalLayout subNodesLayout = new VerticalLayout();
		subNodesLayout.addComponent(new Label("<h2>Podkategorie</h2>",
				Label.CONTENT_XHTML));
		subNodesLayout.addComponent(subNodesTable);
		subNodesTable.setWidth("100%");
		layout.addComponent(subNodesLayout);

		// Obsahy
		VerticalLayout contentsLayout = new VerticalLayout();
		contentsLayout.addComponent(new Label("<h2>Obsahy</h2>",
				Label.CONTENT_XHTML));
		contentsLayout.addComponent(contentsTable);
		contentsTable.setWidth("100%");
		layout.addComponent(contentsLayout);

		// Vytvořit obsahy
		// TODO - vidí pouze role autor
		VerticalLayout newContentsLayout = new VerticalLayout();
		newContentsLayout.addComponent(new Label(
				"<h2>Vytvořit nový obsah</h2>", Label.CONTENT_XHTML));
		newContentsLayout.addComponent(newContentsTable);
		newContentsTable.setWidth("100%");
		layout.addComponent(newContentsLayout);
	}

	@Override
	public DownloadStream handleURI(URL context, String relativeUri) {

		if (relativeUri.length() == 0)
			showError404();

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
				.parseURLIdentifier(relativeUri);
		if (identifier == null)
			showError404();

		NodeDTO node = nodeFacade.getNodeById(identifier.getId());

		// TODO pokud má jiný název, přesměruj na kategorii s ID-Název správným
		// názvem

		updateBreadcrumb(node);
		updateSubNodes(node);
		updateContent(node);
		updateNewContent(node);

		return super.handleURI(context, relativeUri);
	}

	private void updateSubNodes(NodeDTO node) {

		List<NodeDTO> nodes = nodeFacade.getNodesByParentNode(node);
		if (nodes == null)
			showError500();

		subNodesTable.populateTable(nodes, getWindow(CategoryWindow.class)
				.getURL());

	}

	private void updateContent(NodeDTO node) {

		Set<ContentNodeDTO> contentNodes = contentNodeFacade
				.getContentNodesByNode(node);
		if (contentNodes == null)
			showError500();

		contentsTable.populateTable(contentNodes, this);

	}

	private void updateNewContent(NodeDTO node) {

		newContentsTable.populateTable(node, this);

	}

	private void updateBreadcrumb(NodeDTO node) {

		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<BreadcrumbElement>();
		NodeDTO parent = node;
		while (true) {

			// nejprve zkus zjistit, zda předek existuje
			if (parent == null)
				showError404();

			breadcrumbElements.add(new BreadcrumbElement(parent.getName(),
					new ExternalResource(getWindow(CategoryWindow.class)
							.getURL()
							+ URLIdentifierUtils.createURLIdentifier(
									parent.getId(), parent.getName()))));

			// pokud je můj předek null, pak je to konec a je to všechno
			if (parent.getParentID() == null)
				break;

			parent = nodeFacade.getNodeById(parent.getParentID());
		}

		breadcrumb.resetBreadcrumb(breadcrumbElements);
	}
}
