package org.myftp.gattserver.grass3.windows.template;

import java.util.Collection;

import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;

public class NodesTable extends Table {

	private static final long serialVersionUID = -2220485504407844582L;

	/**
	 * Přehled sloupců tabulky obsahů
	 */
	private enum ColumnId {

		IKONA, NÁZEV /* TODO operace */

	}

	public void populateTable(Collection<NodeDTO> nodeList) {

		IndexedContainer container = new IndexedContainer();
		container.addContainerProperty(ColumnId.IKONA, Embedded.class, null);
		container.addContainerProperty(ColumnId.NÁZEV, String.class, "");
		setContainerDataSource(container);
		setColumnWidth(ColumnId.IKONA, 16);
		setColumnHeader(ColumnId.IKONA, "");

		// položky
		for (NodeDTO node : nodeList) {

			Item item = addItem(node);
			item.getItemProperty(ColumnId.NÁZEV).setValue(node.getName());

			Embedded icon = new Embedded();
			icon.setSource(new ThemeResource("img/tags/briefcase_16.png"));
			item.getItemProperty(ColumnId.IKONA).setValue(icon);
		}

	}

}
