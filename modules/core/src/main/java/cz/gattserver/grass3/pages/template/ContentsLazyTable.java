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

		addColumn(contentNode -> {
			ContentService contentService = serviceHolder.getContentServiceByName(contentNode.getContentReaderID());
			return contentService == null ? new ThemeResource(ImageIcons.WARNING_16_ICON)
					: contentService.getContentIcon();
		}, new ImageRenderer<>()).setWidth(16).setCaption("");
		
		addColumn(contentNode -> {
			ContentService contentService = serviceHolder.getContentServiceByName(contentNode.getContentReaderID());
			PageFactory pageFactory = contentService == null ? noServicePageFactory
					: contentService.getContentViewerPageFactory();
			return "<a href='"
					+ page.getPageURL(pageFactory,
							URLIdentifierUtils.createURLIdentifier(contentNode.getContentID(), contentNode.getName()))
					+ "'>" + contentNode.getName() + "</a>";
		}, new HtmlRenderer()).setCaption("Název");
		
		addColumn(contentNode -> {
			NodeOverviewDTO contentParent = contentNode.getParent();
			return "<a href='"
					+ page.getPageResource(nodePageFactory,
							URLIdentifierUtils.createURLIdentifier(contentParent.getId(), contentParent.getName()))
					+ "'>" + contentParent.getName() + "</a>";
		}, new HtmlRenderer());
		
		addColumn(contentNode -> {
			NodeOverviewDTO contentParent = contentNode.getParent();
			return "<a href='"
					+ page.getPageResource(nodePageFactory,
							URLIdentifierUtils.createURLIdentifier(contentParent.getId(), contentParent.getName()))
					+ "'>" + contentParent.getName() + "</a>";
		}, new HtmlRenderer()).setCaption("Kategorie");
		
		addColumn(contentNode -> {
			return contentNode.getAuthor().getName();
		}, new TextRenderer()).setCaption("Autor");
		
		addColumn(ContentNodeOverviewDTO::getCreationDate, new DateRenderer("d.M.yyyy")).setCaption("Datum vytvoření");
		
		addColumn(ContentNodeOverviewDTO::getLastModificationDate, new DateRenderer("d.M.yyyy"))
				.setCaption("Datum úpravy");

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
