package org.myftp.gattserver.grass3.pages.template;

import java.util.Collection;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.IServiceHolder;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.security.ICoreACL;
import org.myftp.gattserver.grass3.service.IContentService;
import org.myftp.gattserver.grass3.util.ComparableLink;
import org.myftp.gattserver.grass3.util.ComparableStringDate;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.springframework.stereotype.Component;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;

@Component("contentsTableFactory")
public class ContentsTableFactory {

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	@Resource(name = "categoryPageFactory")
	private IPageFactory categoryPageFactory;

	@Resource(name = "noServicePageFactory")
	private IPageFactory noServicePageFactory;

	@Resource(name = "serviceHolder")
	private IServiceHolder serviceHolder;

	/**
	 * Vytvoří {@link ContentsTable}, včetně sloupce s odkazem na kategorii
	 * obsahu - pakliže není žádoucí kategorii zobrazovat, je možné použít
	 * parametrizovaný konstruktor
	 */
	public ContentsTable createContentsTable() {

		ContentsTable table = new ContentsTable();

		table.categoryPageFactory = categoryPageFactory;
		table.noServicePageFactory = noServicePageFactory;
		table.serviceHolder = serviceHolder;
		table.coreACL = coreACL;
		table.categoryColumn = true;

		return table;
	}

	public ContentsTable createContentsTableWithoutCategoryColumn() {
		ContentsTable table = createContentsTable();
		table.categoryColumn = false;
		return table;
	}

	/**
	 * Přehled sloupců tabulky obsahů
	 */
	public static enum ColumnId {

		IKONA, NÁZEV, KATEGORIE, AUTOR, DATUM_VYTVOŘENÍ, DATUM_ÚPRAVY;

	}

	public static class ContentsTable extends Table {

		private static final long serialVersionUID = -2220485504407844582L;

		private ICoreACL coreACL;
		private IPageFactory categoryPageFactory;
		private IPageFactory noServicePageFactory;
		private IServiceHolder serviceHolder;
		private boolean categoryColumn;

		private ContentsTable() {
			setHeight("160px");
		}

		public void populateTable(Collection<ContentNodeDTO> contentList,
				AbstractGrassPage page) {

			IndexedContainer container = new IndexedContainer();
			container
					.addContainerProperty(ColumnId.IKONA, Embedded.class, null);
			container.addContainerProperty(ColumnId.NÁZEV,
					ComparableLink.class, null);
			if (categoryColumn) {
				container.addContainerProperty(ColumnId.KATEGORIE,
						ComparableLink.class, null);
			}
			container.addContainerProperty(ColumnId.AUTOR, String.class, "");
			container.addContainerProperty(ColumnId.DATUM_VYTVOŘENÍ,
					ComparableStringDate.class, new ComparableStringDate(null));
			container.addContainerProperty(ColumnId.DATUM_ÚPRAVY,
					ComparableStringDate.class, new ComparableStringDate(null));
			setContainerDataSource(container);
			setColumnWidth(ColumnId.IKONA, 16);
			setColumnHeader(ColumnId.IKONA, "");
			setColumnHeader(ColumnId.DATUM_VYTVOŘENÍ, "DATUM VYTVOŘENÍ");
			setColumnHeader(ColumnId.DATUM_ÚPRAVY, "DATUM ÚPRAVY");

			// položky
			for (ContentNodeDTO contentNode : contentList) {

				if (coreACL.canShowContent(contentNode, page.getUser()) == false)
					continue;

				// jaká prohlížecí služba odpovídá tomuto obsahu
				IContentService contentService = serviceHolder
						.getContentServiceByName(contentNode
								.getContentReaderID());

				IPageFactory pageFactory = null;
				if (contentService == null)
					pageFactory = noServicePageFactory;
				else
					pageFactory = contentService.getContentViewerPageFactory();

				Item item = addItem(contentNode);
				item.getItemProperty(ColumnId.NÁZEV).setValue(
						new ComparableLink(contentNode.getName(), page
								.getPageResource(pageFactory,
										URLIdentifierUtils.createURLIdentifier(
												contentNode.getContentID(),
												contentNode.getName()))));
				if (categoryColumn) {
					NodeDTO contentParent = contentNode.getParent();
					item.getItemProperty(ColumnId.KATEGORIE)
							.setValue(
									new ComparableLink(
											contentParent.getName(),
											page.getPageResource(
													categoryPageFactory,
													URLIdentifierUtils
															.createURLIdentifier(
																	contentParent
																			.getId(),
																	contentParent
																			.getName()))));
				}
				item.getItemProperty(ColumnId.AUTOR).setValue(
						contentNode.getAuthor().getName());
				item.getItemProperty(ColumnId.DATUM_VYTVOŘENÍ)
						.setValue(
								new ComparableStringDate(contentNode
										.getCreationDate()));
				item.getItemProperty(ColumnId.DATUM_ÚPRAVY).setValue(
						new ComparableStringDate(contentNode
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

}
