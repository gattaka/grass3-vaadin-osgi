package cz.gattserver.grass3.pages.template;

import com.vaadin.server.SerializableSupplier;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.ServiceHolder;
import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;
import cz.gattserver.grass3.model.dto.NodeOverviewDTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.service.ContentService;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.ui.ImageIcons;

public class ContentsLazyTable extends Grid<ContentNodeOverviewDTO> {

	private static final long serialVersionUID = -5648982639686386190L;

	// @Resource(name = "nodePageFactory")
	private PageFactory nodePageFactory;

	// @Resource(name = "noServicePageFactory")
	private PageFactory noServicePageFactory;

	// @Resource(name = "serviceHolder")
	private ServiceHolder serviceHolder;

	public ContentsLazyTable() {
		super(ContentNodeOverviewDTO.class);
		addStyleName(ValoTheme.TABLE_COMPACT);
		nodePageFactory = (PageFactory) SpringContextHelper.getBean("nodePageFactory");
		noServicePageFactory = (PageFactory) SpringContextHelper.getBean("noServicePageFactory");
		serviceHolder = (ServiceHolder) SpringContextHelper.getContext().getBean(ServiceHolder.class);
	}

	public void populate(final AbstractGrassPage page, FetchItemsCallback<ContentNodeOverviewDTO> fetchItems,
			SerializableSupplier<Integer> sizeCallback) {

		setDataProvider(fetchItems, sizeCallback);
		setSelectionMode(SelectionMode.NONE);

		String iconBind = "customIcon";
		String nameBind = "customName";
		String nodeBind = "customNode";
		String authorBind = "customAuthor";
		String creationDateBind = "customCreationDate";
		String lastModificationDateBind = "customLastModificationDate";

		addColumn(contentNode -> {
			ContentService contentService = serviceHolder.getContentServiceByName(contentNode.getContentReaderID());
			return contentService == null ? new ThemeResource(ImageIcons.WARNING_16_ICON)
					: contentService.getContentIcon();
		}, new ImageRenderer<>()).setWidth(15 + 16 + 15).setCaption("").setId(iconBind);

		addColumn(contentNode -> {
			ContentService contentService = serviceHolder.getContentServiceByName(contentNode.getContentReaderID());
			PageFactory pageFactory = contentService == null ? noServicePageFactory
					: contentService.getContentViewerPageFactory();
			return "<a href='"
					+ page.getPageURL(pageFactory,
							URLIdentifierUtils.createURLIdentifier(contentNode.getContentID(), contentNode.getName()))
					+ "'>" + contentNode.getName() + "</a>";
		}, new HtmlRenderer()).setCaption("Název").setId(nameBind);

		addColumn(contentNode -> {
			NodeOverviewDTO contentParent = contentNode.getParent();
			return "<a href='"
					+ page.getPageResource(nodePageFactory,
							URLIdentifierUtils.createURLIdentifier(contentParent.getId(), contentParent.getName()))
					+ "'>" + contentParent.getName() + "</a>";
		}, new HtmlRenderer()).setCaption("Kategorie").setId(nodeBind);

		addColumn(contentNode -> {
			return contentNode.getAuthor().getName();
		}, new TextRenderer()).setCaption("Autor").setId(authorBind);

		addColumn(ContentNodeOverviewDTO::getCreationDate, new DateRenderer("%1$te.%1$tm.%1$tY"))
				.setCaption("Datum vytvoření").setId(creationDateBind).setStyleGenerator(item -> "v-align-right");

		addColumn(ContentNodeOverviewDTO::getLastModificationDate, new DateRenderer("%1$te.%1$tm.%1$tY"))
				.setCaption("Datum úpravy").setId(lastModificationDateBind).setStyleGenerator(item -> "v-align-right");

		setColumns(iconBind, nameBind, nodeBind, authorBind, creationDateBind, lastModificationDateBind);

		int min = 50;
		int element = 25;
		int max = 400;
		int header = 25;

		int size = sizeCallback.get() * element;

		if (size < min)
			size = min;
		if (size > max)
			size = max;
		size += header;
		setHeight(size + "px");

	}

}
