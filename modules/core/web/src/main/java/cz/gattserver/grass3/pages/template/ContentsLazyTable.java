package cz.gattserver.grass3.pages.template;

import java.sql.Date;

import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;

import cz.gattserver.grass3.IServiceHolder;
import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;
import cz.gattserver.grass3.model.dto.NodeOverviewDTO;
import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.service.IContentService;
import cz.gattserver.grass3.ui.util.ComparableLink;
import cz.gattserver.grass3.ui.util.ComparableStringDate;
import cz.gattserver.grass3.ui.util.StringToDateConverter;
import cz.gattserver.web.common.URLIdentifierUtils;

public abstract class ContentsLazyTable extends Table {

	private static final long serialVersionUID = -5648982639686386190L;

	// @Resource(name = "nodePageFactory")
	private IPageFactory nodePageFactory;

	// @Resource(name = "noServicePageFactory")
	private IPageFactory noServicePageFactory;

	// @Resource(name = "serviceHolder")
	private IServiceHolder serviceHolder;

	private static final String iconBind = "icon";
	private static final String nameBind = "name";
	private static final String authorBind = "author";
	private static final String nodeBind = "node";
	private static final String creationDateBind = "creationDate";
	private static final String lastModificationDateBind = "lastModificationDate";

	public ContentsLazyTable() {
		nodePageFactory = (IPageFactory) SpringContextHelper.getBean("nodePageFactory");
		noServicePageFactory = (IPageFactory) SpringContextHelper.getBean("noServicePageFactory");
		serviceHolder = (IServiceHolder) SpringContextHelper.getBean("serviceHolder");
	}

	protected ContentNodeOverviewDTO getValueFromId(Object itemId) {
		Item item = getItem(itemId);
		@SuppressWarnings("unchecked")
		ContentNodeOverviewDTO contentNode = ((BeanItem<ContentNodeOverviewDTO>) item).getBean();
		return contentNode;
	}

	public void populate(final AbstractGrassPage page) {
		LazyQueryContainer container = new LazyQueryContainer(createBeanQuery(), "id", 100, false);
		setContainerDataSource(container);

		container.addContainerProperty(creationDateBind, Date.class, null, true, false);
		container.addContainerProperty(lastModificationDateBind, Date.class, null, true, false);

		addGeneratedColumn(iconBind, new ColumnGenerator() {
			private static final long serialVersionUID = 1546758854069400666L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				ContentNodeOverviewDTO contentNode = getValueFromId(itemId);

				// jaká prohlížecí služba odpovídá tomuto obsahu
				IContentService contentService = serviceHolder
						.getContentServiceByName(contentNode.getContentReaderID());

				Embedded icon = new Embedded();
				if (contentService == null) {
					// TODO - stránka s err, že chybí modul
					icon.setSource(new ThemeResource("img/tags/warning_16.png"));
				} else {
					icon.setSource(contentService.getContentIcon());
				}

				return icon;
			}
		});

		addGeneratedColumn(nameBind, new ColumnGenerator() {
			private static final long serialVersionUID = 3828166566373209533L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				ContentNodeOverviewDTO contentNode = getValueFromId(itemId);

				// jaká prohlížecí služba odpovídá tomuto obsahu
				IContentService contentService = serviceHolder
						.getContentServiceByName(contentNode.getContentReaderID());

				IPageFactory pageFactory = null;
				if (contentService == null)
					pageFactory = noServicePageFactory;
				else
					pageFactory = contentService.getContentViewerPageFactory();

				return new ComparableLink(contentNode.getName(), page.getPageResource(pageFactory,
						URLIdentifierUtils.createURLIdentifier(contentNode.getContentID(), contentNode.getName())));
			}
		});

		addGeneratedColumn(nodeBind, new ColumnGenerator() {
			private static final long serialVersionUID = 3828166566373209533L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				ContentNodeOverviewDTO contentNode = getValueFromId(itemId);
				NodeOverviewDTO contentParent = contentNode.getParent();
				return new ComparableLink(contentParent.getName(), page.getPageResource(nodePageFactory,
						URLIdentifierUtils.createURLIdentifier(contentParent.getId(), contentParent.getName())));
			}
		});

		addGeneratedColumn(authorBind, new ColumnGenerator() {
			private static final long serialVersionUID = -11763002613314029L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				ContentNodeOverviewDTO contentNode = getValueFromId(itemId);
				return contentNode.getAuthor();
			}
		});

		setVisibleColumns(
				new Object[] { iconBind, nameBind, nodeBind, authorBind, creationDateBind, lastModificationDateBind });

		setColumnWidth(iconBind, 16);

		setConverter(creationDateBind, new StringToDateConverter(ComparableStringDate.format));
		setConverter(lastModificationDateBind, new StringToDateConverter(ComparableStringDate.format));

		setColumnHeader(iconBind, "");
		setColumnHeader(nameBind, "Název");
		setColumnHeader(nodeBind, "Kategorie");
		setColumnHeader(authorBind, "Autor");
		setColumnHeader(creationDateBind, "Datum vytvoření");
		setColumnHeader(lastModificationDateBind, "Datum úpravy");

	}

	protected abstract BeanQueryFactory<?> createBeanQuery();

}
