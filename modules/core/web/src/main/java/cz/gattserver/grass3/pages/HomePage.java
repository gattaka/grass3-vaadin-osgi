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
import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.facades.IContentNodeFacade;
import cz.gattserver.grass3.facades.IContentTagFacade;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.ContentTagDTO;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.pages.template.BasePage;
import cz.gattserver.grass3.pages.template.ContentsLazyTable;
import cz.gattserver.grass3.pages.template.ContentsTableFactory;
import cz.gattserver.grass3.pages.template.RecentAddedContentsQuery;
import cz.gattserver.grass3.pages.template.RecentModifiedContentsQuery;
import cz.gattserver.grass3.pages.template.ContentsTableFactory.ContentsTable;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.URLIdentifierUtils;

public class HomePage extends BasePage {

	private static final long serialVersionUID = 5355366043081283263L;

	private static Logger logger = LoggerFactory.getLogger(StopWatch.DEFAULT_LOGGER_NAME);

	/**
	 * Fasády
	 */
	@Resource(name = "contentNodeFacade")
	private IContentNodeFacade contentNodeFacade;

	@Resource(name = "contentTagFacade")
	private IContentTagFacade contentTagFacade;

	@Resource(name = "tagPageFactory")
	private IPageFactory tagPageFactory;

	@Resource(name = "contentsTableFactory")
	private ContentsTableFactory contentsTableFactory;

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

		VerticalLayout pagelayout = new VerticalLayout();

		pagelayout.setMargin(true);
		pagelayout.setSpacing(true);

		// Oblíbené
		UserInfoDTO user = getGrassUI().getUser();
		if (user != null) {
			VerticalLayout favouritesLayout = new VerticalLayout();
			favouritesLayout.addComponent(new Label("<h2>Oblíbené obsahy</h2>", ContentMode.HTML));
			ContentsTable favouritesContentsTable = contentsTableFactory.createContentsTable();
			favouritesLayout.addComponent(favouritesContentsTable);
			favouritesContentsTable.setWidth("100%");
			pagelayout.addComponent(favouritesLayout);
			List<ContentNodeDTO> contentNodes = contentNodeFacade.getUserFavouriteContents(user.getId());
			favouritesContentsTable.populateTable(contentNodes, this);
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

		contentLayout.addComponent(pagelayout, "content");

	}

	private void createTagCloud(VerticalLayout pagelayout) {

		VerticalLayout tagCloudLayout = new VerticalLayout();
		tagCloudLayout.addComponent(new Label("<h2>Tagy</h2>", ContentMode.HTML));
		CssLayout tagCloud = new CssLayout();
		tagCloudLayout.addComponent(tagCloud);
		pagelayout.addComponent(tagCloudLayout);

		final List<ContentTagDTO> contentTags = contentTagFacade.getContentTagsForOverview();

		if (contentTags == null)
			showError500();

		tagCloud.removeAllComponents();
		tagCloud.setWidth("100%");

		if (contentTags.isEmpty()) {
			Label noTagsLabel = new Label("Nebyly nalezeny žádné tagy");
			tagCloud.addComponent(noTagsLabel);
		}

		/**
		 * O(n)
		 * 
		 * Pro škálování je potřeba znát počty obsahů ze všech tagů
		 */
		Set<Integer> counts = new HashSet<Integer>();
		for (ContentTagDTO contentTag : contentTags) {
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
		Collections.sort(contentTags, new Comparator<ContentTagDTO>() {
			public int compare(ContentTagDTO o1, ContentTagDTO o2) {
				return o1.getContentNodesCount() - o2.getContentNodesCount();
			}
		});

		/**
		 * Údaj o poslední příčce a velikosti, která jí odpovídala - dle toho budu vědět kdy posunout ohodnocovací
		 * koeficient
		 */
		int lastSize = contentTags.isEmpty() ? 1 : contentTags.get(0).getContentNodesCount();
		int lastFontSize = MIN_FONT_SIZE_TAG_CLOUD;

		/**
		 * O(n)
		 * 
		 * Potřebuju aby bylo možné nějak zavolat svůj počet obsahů a zpátky se vrátila velikost fontu, reps. kategorie
		 * velikosti.
		 */
		final HashMap<Integer, Integer> sizeTable = new HashMap<Integer, Integer>();
		for (ContentTagDTO contentTag : contentTags) {

			/**
			 * Spočítej jeho fontsize - pokud jsem vyšší, pak přihoď velikost koef a ulož můj stav aby ostatní věděli,
			 * jestli mají zvyšovat, nebo zůstat, protože mají stejnou velikost
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
		Collections.sort(contentTags, new Comparator<ContentTagDTO>() {
			public int compare(ContentTagDTO o1, ContentTagDTO o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});

		StringBuilder sb = new StringBuilder();
		for (ContentTagDTO contentTag : contentTags) {
			int size = sizeTable.get(contentTag.getContentNodesCount());
			sb.append("<a title='"
					+ contentTag.getContentNodesCount()
					+ "'href='"
					+ getPageURL(tagPageFactory,
							URLIdentifierUtils.createURLIdentifier(contentTag.getId(), contentTag.getName()))
					+ "' style='font-size:" + size + "pt'>" + contentTag.getName() + "</a> ");
		}

		Label tagLabel;
		tagCloud.addComponent(tagLabel = new Label(sb.toString(), ContentMode.HTML));
		tagLabel.addStyleName("taglabel");
		tagLabel.setSizeFull();

	}

	private void createRecentMenus(VerticalLayout pagelayout) {

		ContentsLazyTable recentAddedContentsTable = new ContentsLazyTable() {
			private static final long serialVersionUID = -2628924290654351639L;

			@Override
			protected BeanQueryFactory<?> createBeanQuery() {
				return new BeanQueryFactory<RecentAddedContentsQuery>(RecentAddedContentsQuery.class);
			}
		};

		ContentsLazyTable recentModifiedContentsTable = new ContentsLazyTable() {
			private static final long serialVersionUID = -2628924290654351639L;

			@Override
			protected BeanQueryFactory<?> createBeanQuery() {
				return new BeanQueryFactory<RecentModifiedContentsQuery>(RecentModifiedContentsQuery.class);
			}
		};

		VerticalLayout recentAddedLayout = new VerticalLayout();
		recentAddedLayout.addComponent(new Label("<h2>Nedávno přidané obsahy</h2>", ContentMode.HTML));
		recentAddedLayout.addComponent(recentAddedContentsTable);
		recentAddedContentsTable.setWidth("100%");
		recentAddedContentsTable.setHeight("200px");
		pagelayout.addComponent(recentAddedLayout);

		recentAddedContentsTable.populate(this);

		// Nedávno upravené obsahy
		VerticalLayout recentModifiedLayout = new VerticalLayout();
		recentModifiedLayout.addComponent(new Label("<h2>Nedávno upravené obsahy</h2>", ContentMode.HTML));
		recentModifiedLayout.addComponent(recentModifiedContentsTable);
		recentModifiedContentsTable.setWidth("100%");
		recentModifiedContentsTable.setHeight("200px");
		pagelayout.addComponent(recentModifiedLayout);

		recentModifiedContentsTable.populate(this);

	}

}