package cz.gattserver.grass3.pages.template;

import com.vaadin.server.SerializableSupplier;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import cz.gattserver.grass3.ServiceHolder;
import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;
import cz.gattserver.grass3.model.dto.NodeOverviewDTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.service.ContentService;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.ui.ImageIcons;

public class ContentsLazyGrid extends Grid<ContentNodeOverviewDTO> {

	private static final long serialVersionUID = -5648982639686386190L;

	// @Resource(name = "nodePageFactory")
	private PageFactory nodePageFactory;

	// @Resource(name = "noServicePageFactory")
	private PageFactory noServicePageFactory;

	// @Resource(name = "serviceHolder")
	private ServiceHolder serviceHolder;

	public ContentsLazyGrid() {
		super(ContentNodeOverviewDTO.class);
		nodePageFactory = (PageFactory) SpringContextHelper.getBean("nodePageFactory");
		noServicePageFactory = (PageFactory) SpringContextHelper.getBean("noServicePageFactory");
		serviceHolder = (ServiceHolder) SpringContextHelper.getContext().getBean(ServiceHolder.class);
	}

	public void populate(final MenuPage page, FetchItemsCallback<ContentNodeOverviewDTO> fetchItems,
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
		}, new ImageRenderer<>()).setWidth(GridUtils.ICON_COLUMN_WIDTH).setCaption("").setId(iconBind);

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
					+ page.getPageURL(nodePageFactory,
							URLIdentifierUtils.createURLIdentifier(contentParent.getId(), contentParent.getName()))
					+ "'>" + contentParent.getName() + "</a>";
		}, new HtmlRenderer()).setCaption("Kategorie").setId(nodeBind).setWidth(GridUtils.NODE_COLUMN_WIDTH);

		addColumn(contentNode -> {
			return contentNode.getAuthor().getName();
		}, new TextRenderer()).setCaption("Autor").setId(authorBind).setWidth(100);

		addColumn(ContentNodeOverviewDTO::getCreationDate, new LocalDateTimeRenderer("d.MM.yyyy"))
				.setCaption("Datum vytvoření").setId(creationDateBind).setStyleGenerator(item -> "v-align-right")
				.setWidth(GridUtils.DATE_COLUMN_WIDTH);

		addColumn(ContentNodeOverviewDTO::getLastModificationDate, new LocalDateTimeRenderer("d.MM.yyyy"))
				.setCaption("Datum úpravy").setId(lastModificationDateBind).setStyleGenerator(item -> "v-align-right")
				.setWidth(GridUtils.DATE_COLUMN_WIDTH);

		setColumns(iconBind, nameBind, nodeBind, authorBind, creationDateBind, lastModificationDateBind);

		setHeight(GridUtils.processHeight(sizeCallback.get()) + "px");

	}

}
