package org.myftp.gattserver.grass3.windows.template;

import java.text.SimpleDateFormat;
import java.util.Collection;

import org.myftp.gattserver.grass3.ServiceHolder;
import org.myftp.gattserver.grass3.facades.NodeFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.security.CoreACL;
import org.myftp.gattserver.grass3.service.IContentService;
import org.myftp.gattserver.grass3.util.ComparableLink;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.windows.CategoryPage;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;

public class ContentsTable extends Table {

	private static final long serialVersionUID = -2220485504407844582L;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"d.M.yyyy HH:mm:ss");
	private NodeFacade nodeFacade = NodeFacade.INSTANCE;

	public ContentsTable() {
		setHeight("200px");
	}

	/**
	 * Přehled sloupců tabulky obsahů
	 */
	private enum ColumnId {

		IKONA, NÁZEV, KATEGORIE, AUTOR, DATUM_VYTVOŘENÍ, DATUM_ÚPRAVY;

	}

	public void populateTable(Collection<ContentNodeDTO> contentList,
			GrassPage page) {

		IndexedContainer container = new IndexedContainer();
		container.addContainerProperty(ColumnId.IKONA, Embedded.class, null);
		container.addContainerProperty(ColumnId.NÁZEV, ComparableLink.class,
				null);
		container.addContainerProperty(ColumnId.KATEGORIE,
				ComparableLink.class, null);
		container.addContainerProperty(ColumnId.AUTOR, String.class, "");
		container.addContainerProperty(ColumnId.DATUM_VYTVOŘENÍ, String.class,
				"");
		container.addContainerProperty(ColumnId.DATUM_ÚPRAVY, String.class, "");
		setContainerDataSource(container);
		setColumnWidth(ColumnId.IKONA, 16);
		setColumnHeader(ColumnId.IKONA, "");
		setColumnHeader(ColumnId.DATUM_VYTVOŘENÍ, "DATUM VYTVOŘENÍ");
		setColumnHeader(ColumnId.DATUM_ÚPRAVY, "DATUM ÚPRAVY");

		CoreACL acl = page.getUserACL();

		// položky
		for (ContentNodeDTO contentNode : contentList) {

			if (acl.canShowContent(contentNode) == false)
				continue;

			// jaká prohlížecí služba odpovídá tomuto obsahu
			IContentService contentService = ServiceHolder
					.getContentServiceByName(contentNode.getContentReaderID());

			PageFactory pageFactory = null;
			if (contentService == null)
				pageFactory = NoServicePage.FACTORY;
			else
				pageFactory = contentService.getContentViewerPageFactory();

			Item item = addItem(contentNode);
			item.getItemProperty(ColumnId.NÁZEV).setValue(
					new ComparableLink(contentNode.getName(), page
							.getPageResource(pageFactory, URLIdentifierUtils
									.createURLIdentifier(
											contentNode.getContentID(),
											contentNode.getName()))));
			NodeDTO contentParent = nodeFacade.getNodeById(contentNode
					.getParentID());
			item.getItemProperty(ColumnId.KATEGORIE).setValue(
					new ComparableLink(contentParent.getName(), page
							.getPageResource(CategoryPage.FACTORY,
									URLIdentifierUtils.createURLIdentifier(
											contentParent.getId(),
											contentParent.getName()))));
			item.getItemProperty(ColumnId.AUTOR).setValue(
					contentNode.getAuthor().getName());
			item.getItemProperty(ColumnId.DATUM_VYTVOŘENÍ).setValue(
					dateFormat.format(contentNode.getCreationDate()));
			item.getItemProperty(ColumnId.DATUM_ÚPRAVY).setValue(
					contentNode.getLastModificationDate() == null ? ""
							: dateFormat.format(contentNode
									.getLastModificationDate()));

			Embedded icon = new Embedded();
			if (contentService == null) {
				// TODO - stránka s err, že chybí modul
				icon.setSource(new ThemeResource("img/tags/warning_16.png"));
			} else {
				icon.setSource(contentService.getContentIcon());
			}
			item.getItemProperty(ColumnId.IKONA).setValue(icon);

		}

	}
}
