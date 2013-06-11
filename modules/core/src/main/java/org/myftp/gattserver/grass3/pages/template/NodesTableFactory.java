package org.myftp.gattserver.grass3.pages.template;

import java.util.Collection;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.ui.util.ComparableLink;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.springframework.stereotype.Component;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;

@Component("nodesTableFactory")
public class NodesTableFactory {

	@Resource(name = "categoryPageFactory")
	private IPageFactory categoryPageFactory;

	public NodesTable createNodesTable() {

		NodesTable table = new NodesTable();
		table.categoryPageFactory = categoryPageFactory;
		return table;
	}

	public static class NodesTable extends Table {

		private static final long serialVersionUID = -2220485504407844582L;

		private IPageFactory categoryPageFactory;

		public NodesTable() {
			setHeight("200px");
		}

		/**
		 * Přehled sloupců tabulky obsahů
		 */
		private enum ColumnId {

			IKONA, NÁZEV /* TODO operace */

		}

		public void populateTable(Collection<NodeDTO> nodeList, AbstractGrassPage page) {

			IndexedContainer container = new IndexedContainer();
			container
					.addContainerProperty(ColumnId.IKONA, Embedded.class, null);
			container.addContainerProperty(ColumnId.NÁZEV,
					ComparableLink.class, null);
			setContainerDataSource(container);
			setColumnWidth(ColumnId.IKONA, 16);
			setColumnHeader(ColumnId.IKONA, "");

			// položky
			for (NodeDTO node : nodeList) {

				Item item = addItem(node);
				item.getItemProperty(ColumnId.NÁZEV)
						.setValue(
								new ComparableLink(
										node.getName(),
										page.getPageResource(
												categoryPageFactory,
												URLIdentifierUtils
														.createURLIdentifier(
																node.getId(),
																node.getName()))));

				Embedded icon = new Embedded();
				icon.setSource(new ThemeResource("img/tags/briefcase_16.png"));
				item.getItemProperty(ColumnId.IKONA).setValue(icon);
			}

		}
	}

}
