package cz.gattserver.grass3.pages;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import cz.gattserver.grass3.components.ContentsLazyGrid;
import cz.gattserver.grass3.facades.ContentNodeFacade;
import cz.gattserver.grass3.facades.ContentTagFacade;
import cz.gattserver.grass3.model.dto.ContentTagOverviewDTO;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.pages.template.BasePage;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.ui.H2Label;

public class HomePage extends BasePage {

	private static Logger logger = LoggerFactory.getLogger(StopWatch.DEFAULT_LOGGER_NAME);

	@Autowired
	private ContentTagFacade contentTagFacade;

	@Autowired
	private ContentNodeFacade contentNodeFacade;

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
		UserInfoDTO user = UIUtils.getUser();
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

		final List<ContentTagOverviewDTO> contentTags = contentTagFacade.getContentTagsForOverview();

		if (contentTags == null)
			UIUtils.showErrorPage500();

		if (contentTags.isEmpty()) {
			Label noTagsLabel = new Label("Nebyly nalezeny žádné tagy");
			tagCloudLayout.addComponent(noTagsLabel);
		}

		/**
		 * O(n)
		 * 
		 * Pro škálování je potřeba znát počty obsahů ze všech tagů
		 */
		Set<Integer> counts = new HashSet<Integer>();
		for (ContentTagOverviewDTO contentTag : contentTags) {
			counts.add(contentTag.getContentNodesCount());
		}

		/**
		 * Přepočet na vypočtení jednotky převodu
		 */
		double scale = MAX_FONT_SIZE_TAG_CLOUD - MIN_FONT_SIZE_TAG_CLOUD;
		int koef = (int) Math.floor(scale / (counts.size() == 1 ? 1 : (counts.size() - 1)));

		if (koef == 0)
			koef = 1;

		/**
		 * O(n.log(n))
		 * 
		 * Seřaď položky listu dle počtu asociovaných obsahů (vzestupně)
		 */
		Collections.sort(contentTags, new Comparator<ContentTagOverviewDTO>() {
			public int compare(ContentTagOverviewDTO o1, ContentTagOverviewDTO o2) {
				return o1.getContentNodesCount() - o2.getContentNodesCount();
			}
		});

		/**
		 * Údaj o poslední příčce a velikosti, která jí odpovídala - dle toho
		 * budu vědět kdy posunout ohodnocovací koeficient
		 */
		int lastSize = contentTags.isEmpty() ? 1 : contentTags.get(0).getContentNodesCount();
		int lastFontSize = MIN_FONT_SIZE_TAG_CLOUD;

		/**
		 * O(n)
		 * 
		 * Potřebuju aby bylo možné nějak zavolat svůj počet obsahů a zpátky se
		 * vrátila velikost fontu, reps. kategorie velikosti.
		 */
		final HashMap<Integer, Integer> sizeTable = new HashMap<Integer, Integer>();
		for (ContentTagOverviewDTO contentTag : contentTags) {

			/**
			 * Spočítej jeho fontsize - pokud jsem vyšší, pak přihoď velikost
			 * koef a ulož můj stav aby ostatní věděli, jestli mají zvyšovat,
			 * nebo zůstat, protože mají stejnou velikost
			 */
			if (contentTag.getContentNodesCount() > lastSize) {
				lastSize = contentTag.getContentNodesCount();
				if (lastFontSize + koef <= MAX_FONT_SIZE_TAG_CLOUD)
					lastFontSize += koef;
			}

			int size = contentTag.getContentNodesCount();
			sizeTable.put(size, lastFontSize);
		}

		/**
		 * O(n.log(n))
		 * 
		 * Seřaď položky listu dle abecedy (vzestupně a case insensitive)
		 */
		Collections.sort(contentTags, new Comparator<ContentTagOverviewDTO>() {
			public int compare(ContentTagOverviewDTO o1, ContentTagOverviewDTO o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});

		char oldChar = 0;
		char currChar = 0;
		StringBuilder sb = null;
		for (ContentTagOverviewDTO contentTag : contentTags) {
			currChar = contentTag.getName().toUpperCase().charAt(0);
			if (currChar != oldChar) {
				if (oldChar != 0) {
					createTags(sb, oldChar, tagCloudLayout);
				}
				sb = new StringBuilder();
				oldChar = currChar;
			}

			int size = sizeTable.get(contentTag.getContentNodesCount());
			sb.append("<a title='" + contentTag.getContentNodesCount() + "'href='"
					+ getPageURL(tagPageFactory,
							URLIdentifierUtils.createURLIdentifier(contentTag.getId(), contentTag.getName()))
					+ "' style='font-size:" + size + "pt'>" + contentTag.getName() + "</a> ");
		}
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
