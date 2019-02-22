package cz.gattserver.grass3.ui.pages;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.interfaces.ContentTagsCloudItemTO;
import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.ui.components.ContentsLazyGrid;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.BasePage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.ui.H2Label;
import elemental.json.JsonArray;

public class HomePage extends BasePage {

	private static Logger perfLogger = LoggerFactory.getLogger(StopWatch.DEFAULT_LOGGER_NAME);

	@Autowired
	private ContentTagService contentTagFacade;

	@Autowired
	private ContentNodeService contentNodeFacade;

	@Resource(name = "tagPageFactory")
	private PageFactory tagPageFactory;

	/**
	 * Kolik je nejmenší font pro tagcloud ?
	 */
	private static int MIN_FONT_SIZE_TAG_CLOUD = 8;

	/**
	 * Kolik je největší font pro tagcloud ?
	 */
	private static int MAX_FONT_SIZE_TAG_CLOUD = 22;

	public HomePage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void createContent(CustomLayout layout) {
		CustomLayout contentLayout = new CustomLayout("oneColumn");
		layout.addComponent(contentLayout, "content");

		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setMargin(true);

		VerticalLayout pagelayout = new VerticalLayout();
		pagelayout.setMargin(true);
		pagelayout.setSpacing(true);
		marginLayout.addComponent(pagelayout);

		// Oblíbené
		StopWatch stopWatch = new StopWatch("HomePage#createRecentMenus");
		UserInfoTO user = UIUtils.getUser();
		if (coreACL.isLoggedIn(user)) {
			VerticalLayout favouritesLayout = new VerticalLayout();
			favouritesLayout.setMargin(false);
			favouritesLayout.addComponent(new H2Label("Oblíbené obsahy"));
			ContentsLazyGrid favouritesContentsTable = new ContentsLazyGrid();
			favouritesContentsTable
					.populate(this,
							(sortOrder, offset, limit) -> contentNodeFacade
									.getUserFavourite(user.getId(), offset, limit).stream(),
							() -> contentNodeFacade.getUserFavouriteCount(user.getId()));
			favouritesLayout.addComponent(favouritesContentsTable);
			favouritesContentsTable.setWidth("100%");
			pagelayout.addComponent(favouritesLayout);
		}
		perfLogger.info(stopWatch.stop());

		createSearchMenu(pagelayout);

		// Nedávno přidané a upravené obsahy
		stopWatch = new StopWatch("HomePage#createRecentMenus");
		createRecentMenus(pagelayout);
		perfLogger.info(stopWatch.stop());

		// Tag-cloud
		JavaScript.getCurrent().addFunction("cz.gattserver.grass3.lazytagcloud", new JavaScriptFunction() {
			private static final long serialVersionUID = 5850638851716815161L;

			@Override
			public void call(JsonArray arguments) {
				createTagCloud(pagelayout);
			}
		});

		JavaScript.eval("setTimeout(function(){ cz.gattserver.grass3.lazytagcloud(); }, 10);");

		contentLayout.addComponent(marginLayout, "content");
	}

	private void createSearchMenu(VerticalLayout pagelayout) {
		VerticalLayout searchResultsLayout = new VerticalLayout();
		searchResultsLayout.setMargin(false);
		searchResultsLayout.addComponent(new H2Label("Vyhledávání"));
		pagelayout.addComponent(searchResultsLayout);

		TextField searchField = new TextField();
		searchField.setPlaceholder("Název obsahu");
		searchField.setWidth("100%");
		searchResultsLayout.addComponent(searchField);

		final ContentsLazyGrid searchResultsTable = new ContentsLazyGrid();
		searchResultsTable.setWidth("100%");
		searchResultsTable.setVisible(false);
		searchResultsLayout.addComponent(searchResultsTable);

		searchField.addValueChangeListener(new ValueChangeListener<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent<String> event) {
				String value = event.getValue();
				if (StringUtils.isNotBlank(value) && !searchResultsTable.isVisible()) {
					searchResultsTable.setVisible(true);
					searchResultsTable.populate(HomePage.this, (sortOrder, offset, limit) -> {
						return contentNodeFacade.getByName(searchField.getValue(), offset, limit).stream();
					}, () -> contentNodeFacade.getCountByName(searchField.getValue()));
					searchResultsTable.setHeight("200px");
				}
				searchResultsTable.getDataProvider().refreshAll();
			}
		});
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.setValueChangeTimeout(200);

	}

	private void createTagCloud(VerticalLayout pagelayout) {
		VerticalLayout tagCloudLayout = new VerticalLayout();
		tagCloudLayout.setMargin(false);
		tagCloudLayout.addComponent(new H2Label("Tagy"));
		pagelayout.addComponent(tagCloudLayout);

		List<ContentTagsCloudItemTO> contentTags = contentTagFacade.createTagsCloud(MAX_FONT_SIZE_TAG_CLOUD,
				MIN_FONT_SIZE_TAG_CLOUD);
		if (contentTags.isEmpty()) {
			Label noTagsLabel = new Label("Nebyly nalezeny žádné tagy");
			tagCloudLayout.addComponent(noTagsLabel);
			return;
		}

		char oldChar = 0;
		char currChar = 0;
		StringBuilder sb = null;
		for (ContentTagsCloudItemTO contentTag : contentTags) {
			currChar = contentTag.getName().toUpperCase().charAt(0);
			if (currChar != oldChar || oldChar == 0) {
				if (oldChar != 0) {
					createTags(sb, oldChar, tagCloudLayout);
				}
				sb = new StringBuilder();
				oldChar = currChar;
			}

			sb.append("<a title='" + contentTag.getContentsCount() + "'href='"
					+ getPageURL(tagPageFactory,
							URLIdentifierUtils.createURLIdentifier(contentTag.getId(), contentTag.getName()))
					+ "' style='font-size:" + contentTag.getFontSize() + "pt'>" + contentTag.getName() + "</a> ");
		}
		if (sb != null)
			createTags(sb, currChar, tagCloudLayout);
	}

	private void createTags(StringBuilder sb, char tag, Layout tagCloudLayout) {
		Label tagLabel;
		CssLayout tagCloud = new CssLayout();
		tagCloudLayout.addComponent(tagCloud);
		tagCloud.addComponent(tagLabel = new Label("<span class=\"tag-letter\">" + tag + "</span>", ContentMode.HTML));
		tagLabel.setSizeUndefined();
		tagCloud.addComponent(tagLabel = new Label(sb.toString(), ContentMode.HTML));
		tagLabel.addStyleName("taglabel");
		tagLabel.setSizeFull();
	}

	private void createRecentMenus(VerticalLayout pagelayout) {

		ContentsLazyGrid recentAddedContentsTable = new ContentsLazyGrid();
		recentAddedContentsTable.populate(this, (sortOrder, offset, limit) -> {
			return contentNodeFacade.getRecentAdded(offset, limit).stream();
		}, contentNodeFacade::getCount);

		ContentsLazyGrid recentModifiedContentsTable = new ContentsLazyGrid();
		recentModifiedContentsTable.populate(this, (sortOrder, offset, limit) -> {
			return contentNodeFacade.getRecentModified(offset, limit).stream();
		}, contentNodeFacade::getCount);

		VerticalLayout recentAddedLayout = new VerticalLayout();
		recentAddedLayout.setMargin(false);
		recentAddedLayout.addComponent(new H2Label("Nedávno přidané obsahy"));
		recentAddedLayout.addComponent(recentAddedContentsTable);
		recentAddedContentsTable.setWidth("100%");
		recentAddedContentsTable.setHeight("200px");
		pagelayout.addComponent(recentAddedLayout);

		// Nedávno upravené obsahy
		VerticalLayout recentModifiedLayout = new VerticalLayout();
		recentModifiedLayout.setMargin(false);
		recentModifiedLayout.addComponent(new H2Label("Nedávno upravené obsahy"));
		recentModifiedLayout.addComponent(recentModifiedContentsTable);
		recentModifiedContentsTable.setWidth("100%");
		recentModifiedContentsTable.setHeight("200px");
		pagelayout.addComponent(recentModifiedLayout);

	}

}
