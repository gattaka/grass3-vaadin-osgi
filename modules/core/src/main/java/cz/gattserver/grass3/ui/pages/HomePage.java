package cz.gattserver.grass3.ui.pages;

import java.util.List;

import javax.annotation.Resource;

import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
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

public class HomePage extends BasePage {

	private static Logger logger = LoggerFactory.getLogger(StopWatch.DEFAULT_LOGGER_NAME);

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
		UserInfoTO user = UIUtils.getUser();
		if (coreACL.isLoggedIn(user)) {
			VerticalLayout favouritesLayout = new VerticalLayout();
			favouritesLayout.setMargin(false);
			favouritesLayout.addComponent(new H2Label("Oblíbené obsahy"));
			ContentsLazyGrid favouritesContentsTable = new ContentsLazyGrid();
			favouritesContentsTable.populate(this, (sortOrder, offset, limit) -> {
				return contentNodeFacade.getUserFavourite(user.getId(), offset / limit, limit).stream();
			}, () -> {
				return contentNodeFacade.getUserFavouriteCount(user.getId());
			});
			favouritesLayout.addComponent(favouritesContentsTable);
			favouritesContentsTable.setWidth("100%");

			int min = 50;
			int element = 25;
			int max = 200;
			int header = 25;

			int size = contentNodeFacade.getUserFavouriteCount(user.getId()) * element;

			if (size < min)
				size = min;
			if (size > max)
				size = max;
			size += header;
			favouritesContentsTable.setHeight(size + "px");

			pagelayout.addComponent(favouritesLayout);
		}

		// Nedávno přidané a upravené obsahy
		StopWatch stopWatch = new StopWatch("HomePage#createRecentMenus");
		createRecentMenus(pagelayout);
		String log = stopWatch.stop();
		System.out.println(log);
		logger.info(log);

		// Tag-cloud
		stopWatch = new StopWatch("HomePage#createTagCloud");
		createTagCloud(pagelayout);
		log = stopWatch.stop();
		System.out.println(log);
		logger.info(log);

		contentLayout.addComponent(marginLayout, "content");

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
			if (currChar != oldChar) {
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
			return contentNodeFacade.getRecentAdded(offset / limit, limit).stream();
		}, contentNodeFacade::getCount);

		ContentsLazyGrid recentModifiedContentsTable = new ContentsLazyGrid();
		recentModifiedContentsTable.populate(this, (sortOrder, offset, limit) -> {
			return contentNodeFacade.getRecentModified(offset / limit, limit).stream();
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
