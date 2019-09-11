package cz.gattserver.grass3.ui.components;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

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

	public void populate(final MenuPage page, FetchCallback<ContentNodeOverviewTO, Void> fetchCallback,
			CountCallback<ContentNodeOverviewTO, Void> countCallback) {

		PageFactory nodePageFactory = ((PageFactory) SpringContextHelper.getBean("nodePageFactory"));
		PageFactory noServicePageFactory = (PageFactory) SpringContextHelper.getBean("noServicePageFactory");
		ModuleRegister serviceHolder = SpringContextHelper.getContext().getBean(ModuleRegister.class);

		setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));
		setSelectionMode(SelectionMode.NONE);

		String lockIconBind = "lockIcon";
		String iconBind = "customIcon";
		String nameBind = "customName";
		String nodeBind = "customNode";
		String authorBind = "customAuthor";
		String creationDateBind = "customCreationDate";
		String lastModificationDateBind = "customLastModificationDate";

		if (UIUtils.getUser() != null) {
			addColumn(new IconRenderer<ContentNodeOverviewTO>(c -> c.isPublicated() ? new Span()
					: new Image(ImageIcon.SHIELD_16_ICON.createResource(), "locked")))
							.setWidth(GridUtils.ICON_COLUMN_WIDTH + "px").setHeader("").setKey(lockIconBind)
							.setClassNameGenerator(item -> "icon-cell");
		}

		addColumn(new IconRenderer<ContentNodeOverviewTO>(c -> {
			ContentModule contentService = serviceHolder.getContentModulesByName(c.getContentReaderID());
			Image img = new Image(contentService == null ? ImageIcon.WARNING_16_ICON.createResource()
					: contentService.getContentIcon(), "");
			img.setWidth("16px");
			return img;
		})).setHeader("").setKey(iconBind).setClassNameGenerator(item -> "icon-cell");

		addColumn(new ComponentRenderer<Anchor, ContentNodeOverviewTO>(contentNode -> {
			ContentModule contentService = serviceHolder.getContentModulesByName(contentNode.getContentReaderID());
			PageFactory pageFactory = contentService == null ? noServicePageFactory
					: contentService.getContentViewerPageFactory();
			String url = page.getPageURL(pageFactory,
					URLIdentifierUtils.createURLIdentifier(contentNode.getContentID(), contentNode.getName()));
			return new Anchor(url, contentNode.getName());
		})).setHeader("Název").setId(nameBind);

		addColumn(new ComponentRenderer<Anchor, ContentNodeOverviewTO>(contentNode -> {
			String url = page.getPageURL(nodePageFactory, URLIdentifierUtils
					.createURLIdentifier(contentNode.getParentNodeId(), contentNode.getParentNodeName())) + "'>"
					+ contentNode.getParentNodeName();
			return new Anchor(url, contentNode.getName());
		})).setHeader("Kategorie").setId(nodeBind);

		addColumn(ContentNodeOverviewTO::getAuthorName).setHeader("Autor").setKey(authorBind).setWidth("100px");

		addColumn(new LocalDateTimeRenderer<>(ContentNodeOverviewTO::getCreationDate, "d.M.yyyy"))
				.setHeader("Vytvořeno").setKey(creationDateBind).setClassNameGenerator(item -> "v-align-right")
				.setFlexGrow(0).setWidth("90px");

		addColumn(new LocalDateTimeRenderer<>(ContentNodeOverviewTO::getLastModificationDate, "d.M.yyyy"))
				.setHeader("Upraveno").setKey(creationDateBind).setClassNameGenerator(item -> "v-align-right")
				.setFlexGrow(0).setWidth("90px");

		if (UIUtils.getUser() != null)
			setColumns(iconBind, nameBind, lockIconBind, nodeBind, authorBind, creationDateBind,
					lastModificationDateBind);
		else
			setColumns(iconBind, nameBind, nodeBind, authorBind, creationDateBind, lastModificationDateBind);

		setHeight(GridUtils.processHeight(countCallback.count(new Query<>())) + "px");

	}

}
