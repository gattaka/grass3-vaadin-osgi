package org.myftp.gattserver.grass3.windows.template;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.template.Breadcrumb;
import org.myftp.gattserver.grass3.template.Breadcrumb.BreadcrumbElement;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.windows.CategoryWindow;
import org.myftp.gattserver.grass3.windows.template.TwoColumnWindow;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public abstract class ContentViewerWindow extends TwoColumnWindow {

	private static final long serialVersionUID = 5078280973817331002L;

	private ContentNodeDTO content;
	private Class<? extends GrassWindow> contentViewerClass;

	public ContentViewerWindow(Class<? extends GrassWindow> contentViewerClass) {
		this.contentViewerClass = contentViewerClass;
	}
	
	/**
	 * Breadcrumb
	 */
	private Breadcrumb breadcrumb;

	@Override
	protected void createLeftColumnContent(VerticalLayout layout) {
	}

	@Override
	protected void createRightColumnContent(VerticalLayout layout) {

		layout.setMargin(true);
		layout.setSpacing(true);
		
		layout.addComponent(breadcrumb = new Breadcrumb());

	}

	@Override
	public DownloadStream handleURI(URL context, String relativeUri) {

		content = getContentNodeDTO();

		updateBreadcrumb(content);

		return super.handleURI(context, relativeUri);
	}

	protected abstract ContentNodeDTO getContentNodeDTO();

	private void updateBreadcrumb(ContentNodeDTO content) {

		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<BreadcrumbElement>();
		
		/**
		 * obsah
		 */
		breadcrumbElements.add(new BreadcrumbElement(content.getName(),
				new ExternalResource(getWindow(contentViewerClass)
						.getURL()
						+ URLIdentifierUtils.createURLIdentifier(
								content.getId(), content.getName()))));
		
		/**
		 * kategorie
		 */
		NodeDTO parent = nodeFacade.getNodeById(content.getParentID());
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
