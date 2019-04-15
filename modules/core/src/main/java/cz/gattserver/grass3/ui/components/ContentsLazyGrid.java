package cz.gattserver.grass3.ui.components;

import com.vaadin.server.SerializableSupplier;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Image;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import cz.gattserver.grass3.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass3.modules.ContentModule;
import cz.gattserver.grass3.modules.register.ModuleRegister;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.MenuPage;
import cz.gattserver.grass3.ui.util.GridUtils;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;

public class ContentsLazyGrid extends Grid<ContentNodeOverviewTO> {

	private static final long serialVersionUID = -5648982639686386190L;

	public ContentsLazyGrid() {
		super(ContentNodeOverviewTO.class);
	}

	public void populate(final MenuPage page, FetchItemsCallback<ContentNodeOverviewTO> fetchItems,
			SerializableSupplier<Integer> sizeCallback) {

		PageFactory nodePageFactory = ((PageFactory) SpringContextHelper.getBean("nodePageFactory"));
		PageFactory noServicePageFactory = (PageFactory) SpringContextHelper.getBean("noServicePageFactory");
		ModuleRegister serviceHolder = SpringContextHelper.getContext().getBean(ModuleRegister.class);

		setDataProvider(fetchItems, sizeCallback);
		setSelectionMode(SelectionMode.NONE);

		String lockIconBind = "lockIcon";
		String iconBind = "customIcon";
		String nameBind = "customName";
		String nodeBind = "customNode";
		String authorBind = "customAuthor";
		String creationDateBind = "customCreationDate";
		String lastModificationDateBind = "customLastModificationDate";

		if (UIUtils.getUser() != null)
			addColumn(contentNode -> {
				Image img = contentNode.isPublicated() ? null
						: new Image(null, ImageIcon.SHIELD_16_ICON.createResource());
				return img;
			}, new ComponentRenderer()).setWidth(GridUtils.ICON_COLUMN_WIDTH).setCaption("").setId(lockIconBind)
					.setStyleGenerator(item -> "icon-cell");

		addColumn(contentNode -> {
			ContentModule contentService = serviceHolder.getContentModulesByName(contentNode.getContentReaderID());
			Image img = new Image("", contentService == null ? ImageIcon.WARNING_16_ICON.createResource()
					: contentService.getContentIcon());
			img.setWidth("16px");
			return img;
		}, new ComponentRenderer()).setWidth(GridUtils.ICON_COLUMN_WIDTH).setCaption("").setId(iconBind)
				.setStyleGenerator(item -> "icon-cell");

		addColumn(contentNode -> {
			ContentModule contentService = serviceHolder.getContentModulesByName(contentNode.getContentReaderID());
			PageFactory pageFactory = contentService == null ? noServicePageFactory
					: contentService.getContentViewerPageFactory();
			return "<a href='"
					+ page.getPageURL(pageFactory,
							URLIdentifierUtils.createURLIdentifier(contentNode.getContentID(), contentNode.getName()))
					+ "'>" + contentNode.getName() + "</a>";
		}, new HtmlRenderer()).setCaption("Název").setId(nameBind);

		addColumn(contentNode -> {
			return "<a href='"
					+ page.getPageURL(nodePageFactory, URLIdentifierUtils
							.createURLIdentifier(contentNode.getParentNodeId(), contentNode.getParentNodeName()))
					+ "'>" + contentNode.getParentNodeName() + "</a>";
		}, new HtmlRenderer()).setCaption("Kategorie").setId(nodeBind).setWidth(GridUtils.NODE_COLUMN_WIDTH);

		addColumn(ContentNodeOverviewTO::getAuthorName, new TextRenderer()).setCaption("Autor").setId(authorBind)
				.setWidth(100);

		addColumn(ContentNodeOverviewTO::getCreationDate, new LocalDateTimeRenderer("d.M.yyyy"))
				.setCaption("Datum vytvoření").setId(creationDateBind).setStyleGenerator(item -> "v-align-right")
				.setWidth(GridUtils.DATE_COLUMN_WIDTH);

		addColumn(ContentNodeOverviewTO::getLastModificationDate, new LocalDateTimeRenderer("d.M.yyyy"))
				.setCaption("Datum úpravy").setId(lastModificationDateBind).setStyleGenerator(item -> "v-align-right")
				.setWidth(GridUtils.DATE_COLUMN_WIDTH);

		if (UIUtils.getUser() != null)
			setColumns(iconBind, nameBind, lockIconBind, nodeBind, authorBind, creationDateBind,
					lastModificationDateBind);
		else
			setColumns(iconBind, nameBind, nodeBind, authorBind, creationDateBind, lastModificationDateBind);

		setHeight(GridUtils.processHeight(sizeCallback.get()) + "px");

	}

}
