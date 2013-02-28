package org.myftp.gattserver.grass3.windows;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.ContentNodeFacade;
import org.myftp.gattserver.grass3.facades.ContentTagFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.ContentTagDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.template.BasePage;
import org.myftp.gattserver.grass3.windows.template.ContentsTable;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class HomePage extends BasePage {

	private static final long serialVersionUID = 5355366043081283263L;

	/**
	 * Fasády
	 */
	@Resource(name = "contentNodeFacade")
	private ContentNodeFacade contentNodeFacade;

	@Resource(name = "contentTagFacade")
	private ContentTagFacade contentTagFacade;

	/**
	 * Kolik položek mají menu "nedávno" maximálně zobrazit ?
	 */
	private static int RECENT_ITEMS_COUNT = 10;

	/**
	 * Kolik je nejmenší font pro tagcloud ?
	 */
	private static int MIN_FONT_SIZE_TAG_CLOUD = 8;

	/**
	 * Kolik je největší font pro tagcloud ?
	 */
	private static int MAX_FONT_SIZE_TAG_CLOUD = 20;

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
			favouritesLayout.addComponent(new Label("<h2>Oblíbené obsahy</h2>",
					ContentMode.HTML));
			ContentsTable favouritesContentsTable = new ContentsTable();
			favouritesLayout.addComponent(favouritesContentsTable);
			favouritesContentsTable.setWidth("100%");
			pagelayout.addComponent(favouritesLayout);
			Set<ContentNodeDTO> contentNodes = contentNodeFacade
					.getUserFavouriteContents(user);
			favouritesContentsTable.populateTable(contentNodes, this);
		}

		// Nedávno přidané a upravené obsahy
		createRecentMenus(pagelayout);

		// Tag-cloud
		createTagCloud(pagelayout);

		contentLayout.addComponent(pagelayout, "content");

	}

	private void createTagCloud(VerticalLayout pagelayout) {

		VerticalLayout tagCloudLayout = new VerticalLayout();
		tagCloudLayout
				.addComponent(new Label("<h2>Tagy</h2>", ContentMode.HTML));
		CssLayout tagCloud = new CssLayout();
		tagCloudLayout.addComponent(tagCloud);
		pagelayout.addComponent(tagCloudLayout);

		final List<ContentTagDTO> contentTags = contentTagFacade
				.getAllContentTags();

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
			int size = contentTag.getContentNodes().size();
			counts.add(size);
		}

		/**
		 * Přepočet na vypočtení jednotky převodu
		 */
		double scale = MAX_FONT_SIZE_TAG_CLOUD - MIN_FONT_SIZE_TAG_CLOUD;
		final int koef = (int) Math.floor(scale
				/ (counts.size() == 1 ? 1 : (counts.size() - 1)));

		/**
		 * O(n.log(n))
		 * 
		 * Seřaď položky listu dle počtu asociovaných obsahů (vzestupně)
		 */
		Collections.sort(contentTags, new Comparator<ContentTagDTO>() {
			public int compare(ContentTagDTO o1, ContentTagDTO o2) {
				return o1.getContentNodes().size()
						- o2.getContentNodes().size();
			}
		});

		/**
		 * Údaj o poslední příčce a velikosti, která jí odpovídala - dle toho
		 * budu vědět kdy posunout ohodnocovací koeficient
		 */
		int lastSize = contentTags.isEmpty() ? 1 : contentTags.get(0)
				.getContentNodes().size();
		int lastFontSize = MIN_FONT_SIZE_TAG_CLOUD;

		/**
		 * O(n)
		 * 
		 * Potřebuju aby bylo možné nějak zavolat svůj počet obsahů a zpátky se
		 * vrátila velikost fontu, reps. kategorie velikosti.
		 */
		final HashMap<Integer, Integer> sizeTable = new HashMap<Integer, Integer>();
		for (ContentTagDTO contentTag : contentTags) {

			/**
			 * Spočítej jeho fontsize - pokud jsem vyšší, pak přihoď velikost
			 * koef a ulož můj stav aby ostatní věděli, jestli mají zvyšovat,
			 * nebo zůstat, protože mají stejnou velikost
			 */
			if (contentTag.getContentNodes().size() > lastSize) {
				lastSize = contentTag.getContentNodes().size();
				lastFontSize += koef;
			}

			int size = contentTag.getContentNodes().size();
			sizeTable.put(size, lastFontSize);
		}

		/**
		 * O(n.log(n))
		 * 
		 * Seřaď položky listu dle abecedy (vzestupně a case insensitive)
		 */
		Collections.sort(contentTags, new Comparator<ContentTagDTO>() {
			public int compare(ContentTagDTO o1, ContentTagDTO o2) {
				return o1.getName().toLowerCase()
						.compareTo(o2.getName().toLowerCase());
			}
		});

		for (ContentTagDTO contentTag : contentTags) {
			int size = sizeTable.get(contentTag.getContentNodes().size());
			Label tagLabel;
			tagCloud.addComponent(tagLabel = new Label("<a href='"
					+ getPageURL(TagPage.FACTORY, contentTag.getName())
					+ "' style='font-size:" + size + "pt'>"
					+ contentTag.getName() + "</a>", ContentMode.HTML));
			tagLabel.addStyleName("taglabel");
			tagLabel.setSizeUndefined();
		}

	}

	private void createRecentMenus(VerticalLayout pagelayout) {

		ContentsTable recentAddedContentsTable = new ContentsTable();
		ContentsTable recentModifiedContentsTable = new ContentsTable();

		Set<ContentNodeDTO> recentAdded = contentNodeFacade
				.getRecentAdded(RECENT_ITEMS_COUNT);
		Set<ContentNodeDTO> recentModified = contentNodeFacade
				.getRecentModified(RECENT_ITEMS_COUNT);

		VerticalLayout recentAddedLayout = new VerticalLayout();
		recentAddedLayout.addComponent(new Label(
				"<h2>Nedávno přidané obsahy</h2>", ContentMode.HTML));
		recentAddedLayout.addComponent(recentAddedContentsTable);
		recentAddedContentsTable.setWidth("100%");
		pagelayout.addComponent(recentAddedLayout);

		// Nedávno upravené obsahy
		VerticalLayout recentModifiedLayout = new VerticalLayout();
		recentModifiedLayout.addComponent(new Label(
				"<h2>Nedávno upravené obsahy</h2>", ContentMode.HTML));
		recentModifiedLayout.addComponent(recentModifiedContentsTable);
		recentModifiedContentsTable.setWidth("100%");
		pagelayout.addComponent(recentModifiedLayout);

		recentAddedContentsTable.populateTable(recentAdded, this);
		recentModifiedContentsTable.populateTable(recentModified, this);

	}

}
