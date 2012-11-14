package org.myftp.gattserver.grass3.windows.template;

import java.util.Collection;

import org.myftp.gattserver.grass3.ServiceHolder;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.service.IContentService;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;

public class ContentsTable extends Table {

	private static final long serialVersionUID = -2220485504407844582L;

	/**
	 * Přehled sloupců tabulky obsahů
	 */
	private enum ColumnId {

		IKONA, NÁZEV, AUTOR, DATUM_VYTVOŘENÍ, DATUM_ÚPRAVY;

	}

	public void populateTable(Collection<ContentNodeDTO> contentList) {

		IndexedContainer container = new IndexedContainer();
		container.addContainerProperty(ColumnId.IKONA, Embedded.class, null);
		container.addContainerProperty(ColumnId.NÁZEV, String.class, "");
		container.addContainerProperty(ColumnId.AUTOR, String.class, "");
		container.addContainerProperty(ColumnId.DATUM_VYTVOŘENÍ, String.class,
				"");
		container.addContainerProperty(ColumnId.DATUM_ÚPRAVY, String.class, "");
		setContainerDataSource(container);
		setColumnWidth(ColumnId.IKONA, 16);
		setColumnHeader(ColumnId.IKONA, "");
		setColumnHeader(ColumnId.DATUM_VYTVOŘENÍ, "DATUM VYTVOŘENÍ");
		setColumnHeader(ColumnId.DATUM_ÚPRAVY, "DATUM ÚPRAVY");

		// položky
		for (ContentNodeDTO contentNode : contentList) {

			// jaká prohlížecí služba odpovídá tomuto obsahu
			IContentService contentService = ServiceHolder.getInstance()
					.getContentServiceByName(contentNode.getContentReaderID());

			Item item = addItem(contentNode);
			item.getItemProperty(ColumnId.NÁZEV)
					.setValue(contentNode.getName());
			item.getItemProperty(ColumnId.AUTOR).setValue(
					contentNode.getAuthor());
			item.getItemProperty(ColumnId.DATUM_VYTVOŘENÍ).setValue(
					contentNode.getCreationDate());
			item.getItemProperty(ColumnId.DATUM_ÚPRAVY).setValue(
					contentNode.getLastModificationDate());

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
