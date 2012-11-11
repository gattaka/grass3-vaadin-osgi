package org.myftp.gattserver.grass3.windows;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.facades.NodeFacade;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.template.Breadcrumb;
import org.myftp.gattserver.grass3.template.Breadcrumb.BreadcrumbElement;
import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.VerticalLayout;

public class CategoryWindow extends OneColumnWindow {

	private static final long serialVersionUID = -499585200973560016L;

	private NodeFacade nodeFacade = NodeFacade.INSTANCE;

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
	}

	@Override
	public DownloadStream handleURI(URL context, String relativeUri) {

		if (relativeUri.length() == 0)
			showError404();

		// získej ID
		String[] parts = relativeUri.split("-");
		if (parts.length <= 1)
			showError404();

		Long id = null;
		try {
			id = Long.valueOf(parts[0]);
		} catch (NumberFormatException e) {
			showError404();
		}

		NodeDTO node = nodeFacade.findNodeById(id);

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
							+ parent.getId().toString()
							+ "-"
							+ parent.getName())));

			// pokud je můj předek null, pak je to konec a je to všechno
			if (parent.getParentID() == null)
				break;

			parent = nodeFacade.findNodeById(parent.getParentID());
		}

		breadcrumb.resetBreadcrumb(breadcrumbElements);

		return super.handleURI(context, relativeUri);
	}
}
