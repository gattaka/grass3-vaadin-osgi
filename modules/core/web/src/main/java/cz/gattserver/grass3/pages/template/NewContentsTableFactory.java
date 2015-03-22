package cz.gattserver.grass3.pages.template;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;

import cz.gattserver.grass3.IServiceHolder;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.service.IContentService;
import cz.gattserver.grass3.template.DefaultContentOperations;
import cz.gattserver.grass3.ui.util.ComparableLink;
import cz.gattserver.web.common.URLIdentifierUtils;

@Component("newContentsTableFactory")
public class NewContentsTableFactory {

	@Resource(name = "serviceHolder")
	private IServiceHolder serviceHolder;

	public NewContentsTable createNewContentsTable() {

		NewContentsTable table = new NewContentsTable();
		table.serviceHolder = serviceHolder;
		return table;
	}

	public static class NewContentsTable extends Table {

		private static final long serialVersionUID = -2220485504407844582L;

		private IServiceHolder serviceHolder;

		public NewContentsTable() {
			setHeight("200px");
		}

		/**
		 * Přehled sloupců tabulky nových obsahů
		 */
		private enum ColumnId {

			IKONA, NÁZEV

		}

		public void populateTable(NodeDTO node, AbstractGrassPage page) {

			IndexedContainer container = new IndexedContainer();
			container
					.addContainerProperty(ColumnId.IKONA, Embedded.class, null);
			container.addContainerProperty(ColumnId.NÁZEV,
					ComparableLink.class, null);
			setContainerDataSource(container);
			setColumnWidth(ColumnId.IKONA, 16);
			setColumnHeader(ColumnId.IKONA, "");

			// jaké služby obsahů mám k dispozici ?
			List<IContentService> contentServices = serviceHolder
					.getContentServices();

			// položky
			for (IContentService contentService : contentServices) {

				Item item = addItem(contentService);
				item.getItemProperty(ColumnId.NÁZEV)
						.setValue(
								new ComparableLink(
										contentService
												.getCreateNewContentLabel(),
										page.getPageResource(contentService
												.getContentEditorPageFactory(),
												DefaultContentOperations.NEW
														.toString(),
												URLIdentifierUtils
														.createURLIdentifier(
																node.getId(),
																node.getName()))));

				Embedded icon = new Embedded();
				icon.setSource(contentService.getContentIcon());
				item.getItemProperty(ColumnId.IKONA).setValue(icon);
			}

		}
	}
}
