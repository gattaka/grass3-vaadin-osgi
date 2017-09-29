package cz.gattserver.grass3.pages.template;

import java.sql.Date;

import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;

import cz.gattserver.grass3.ServiceHolder;
import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;
import cz.gattserver.grass3.model.dto.NodeOverviewDTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.service.ContentService;
import cz.gattserver.grass3.ui.util.ComparableLink;
import cz.gattserver.grass3.ui.util.ComparableStringDate;
import cz.gattserver.grass3.ui.util.StringToDateConverter;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.ui.ImageIcons;

public abstract class ContentsLazyTable extends Table {

	private static final long serialVersionUID = -5648982639686386190L;

	// @Resource(name = "nodePageFactory")
	private PageFactory nodePageFactory;

	// @Resource(name = "noServicePageFactory")
	private PageFactory noServicePageFactory;

	// @Resource(name = "serviceHolder")
	private ServiceHolder serviceHolder;

	private static final String iconBind = "icon";
	private static final String nameBind = "name";
	private static final String authorBind = "author";
	private static final String nodeBind = "node";
	private static final String creationDateBind = "creationDate";
	private static final String lastModificationDateBind = "lastModificationDate";

	public ContentsLazyTable() {
		nodePageFactory = (PageFactory) SpringContextHelper.getBean("nodePageFactory");
		noServicePageFactory = (PageFactory) SpringContextHelper.getBean("noServicePageFactory");
		serviceHolder = (ServiceHolder) SpringContextHelper.getContext().getBean(ServiceHolder.class);
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
				ContentService contentService = serviceHolder.getContentServiceByName(contentNode.getContentReaderID());

				Embedded icon = new Embedded();
				if (contentService == null) {
					// TODO - stránka s err, že chybí modul
					icon.setSource(new ThemeResource(ImageIcons.WARNING_16_ICON));
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
				ContentService contentService = serviceHolder.getContentServiceByName(contentNode.getContentReaderID());

				PageFactory pageFactory = null;
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

		int min = 50;
		int element = 25;
		int max = 400;
		int header = 25;

		int size = container.getItemIds().size() * element;

		if (size < min)
			size = min;
		if (size > max)
			size = max;
		size += header;
		setHeight(size + "px");

	}

	protected abstract BeanQueryFactory<?> createBeanQuery();

}
