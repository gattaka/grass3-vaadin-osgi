package cz.gattserver.grass3.ui.pages;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import cz.gattserver.grass3.interfaces.ContentTagsCloudItemTO;
import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.ContentsLazyGrid;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.ui.HtmlSpan;

@Route(value = "")
@Theme(value = Lumo.class)
@CssImport("./styles.css")
public class HomePage extends OneColumnPage {

	private static final long serialVersionUID = 3100924667157515504L;

	/**
	 * Kolik je nejmenší font pro tagcloud ?
	 */
	private static final int MIN_FONT_SIZE_TAG_CLOUD = 8;

	/**
	 * Kolik je největší font pro tagcloud ?
	 */
	private static final int MAX_FONT_SIZE_TAG_CLOUD = 22;

	@Autowired
	private ContentTagService contentTagFacade;

	@Autowired
	private ContentNodeService contentNodeFacade;

	@Autowired
	private SecurityService securityService;

	@Resource(name = "tagPageFactory")
	private PageFactory tagPageFactory;

	public HomePage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createColumnContent() {
		VerticalLayout paddingLayout = new VerticalLayout();
		paddingLayout.setPadding(true);

		VerticalLayout pagelayout = new VerticalLayout();
		pagelayout.setPadding(true);
		pagelayout.setSpacing(true);
		paddingLayout.add(pagelayout);

		// Oblíbené
		UserInfoTO user = UIUtils.getUser();
		if (coreACL.isLoggedIn(user)) {
			VerticalLayout favouritesLayout = new VerticalLayout();
			favouritesLayout.setPadding(false);
			favouritesLayout.add(new H2("Oblíbené obsahy"));
			ContentsLazyGrid favouritesContentsTable = new ContentsLazyGrid();
			favouritesContentsTable.populate(this,
					q -> contentNodeFacade.getUserFavourite(user.getId(), q.getOffset(), q.getLimit()).stream(),
					q -> contentNodeFacade.getUserFavouriteCount(user.getId()));
			favouritesLayout.add(favouritesContentsTable);
			favouritesContentsTable.setWidth("100%");
			pagelayout.add(favouritesLayout);
		}

		createSearchMenu(pagelayout);

		// Nedávno přidané a upravené obsahy
		createRecentMenus(pagelayout);

		// Tag-cloud
		Div tagJsDiv = new Div() {
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void tagCloundCallback() {
				createTagCloud(pagelayout);
			}
		};
		String tagJsDivId = "tag-js-div";
		tagJsDiv.setId(tagJsDivId);
		paddingLayout.add(tagJsDiv);

		UI.getCurrent().getPage().executeJs("setTimeout(function(){ document.getElementById('" + tagJsDivId
				+ "').$server.tagCloundCallback() }, 10);");

		return paddingLayout;
	}

	private void createSearchMenu(VerticalLayout pagelayout) {
		VerticalLayout searchResultsLayout = new VerticalLayout();
		searchResultsLayout.setPadding(false);
		searchResultsLayout.add(new H2("Vyhledávání"));
		pagelayout.add(searchResultsLayout);

		TextField searchField = new TextField();
		searchField.setPlaceholder("Název obsahu");
		searchField.setWidth("100%");
		searchResultsLayout.add(searchField);

		final ContentsLazyGrid searchResultsTable = new ContentsLazyGrid();
		searchResultsTable.setWidth("100%");
		searchResultsTable.setVisible(false);
		searchResultsLayout.add(searchResultsTable);

		UserInfoTO user = securityService.getCurrentUser();

		searchField.addValueChangeListener(e -> {
			String value = e.getValue();
			if (StringUtils.isNotBlank(value) && !searchResultsTable.isVisible()) {
				searchResultsTable.setVisible(true);
				searchResultsTable.populate(HomePage.this,
						q -> contentNodeFacade
								.getByName(searchField.getValue(), user.getId(), q.getOffset(), q.getLimit()).stream(),
						q -> contentNodeFacade.getCountByName(searchField.getValue(), user.getId()));
				searchResultsTable.setHeight("200px");
			}
			searchResultsTable.getDataProvider().refreshAll();

		});
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.setValueChangeTimeout(200);

	}

	private void createTagCloud(VerticalLayout pagelayout) {
		VerticalLayout tagCloudLayout = new VerticalLayout();
		tagCloudLayout.setPadding(false);
		tagCloudLayout.add(new H2("Tagy"));
		pagelayout.add(tagCloudLayout);

		List<ContentTagsCloudItemTO> contentTags = contentTagFacade.createTagsCloud(MAX_FONT_SIZE_TAG_CLOUD,
				MIN_FONT_SIZE_TAG_CLOUD);
		if (contentTags.isEmpty()) {
			Span noTagsSpan = new Span("Nebyly nalezeny žádné tagy");
			tagCloudLayout.add(noTagsSpan);
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

	private void createTags(StringBuilder sb, char tag, VerticalLayout tagCloudLayout) {
		Span tagLabel;
		Div tagCloud = new Div();
		tagCloudLayout.add(tagCloud);
		tagLabel = new HtmlSpan("<span class=\"tag-letter\">" + tag + "</span>");
		tagCloud.add(tagLabel);
		tagLabel.setSizeUndefined();
		tagLabel = new HtmlSpan(sb.toString());
		tagCloud.add(tagLabel);
		tagLabel.addClassName("taglabel");
		tagLabel.setSizeFull();
	}

	private void createRecentMenus(VerticalLayout pagelayout) {

		ContentsLazyGrid recentAddedContentsTable = new ContentsLazyGrid();
		recentAddedContentsTable.populate(this,
				q -> contentNodeFacade.getRecentAdded(q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeFacade.getCount());

		ContentsLazyGrid recentModifiedContentsTable = new ContentsLazyGrid();
		recentModifiedContentsTable.populate(this,
				q -> contentNodeFacade.getRecentModified(q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeFacade.getCount());

		VerticalLayout recentAddedLayout = new VerticalLayout();
		recentAddedLayout.setPadding(false);
		recentAddedLayout.add(new H2("Nedávno přidané obsahy"));
		recentAddedLayout.add(recentAddedContentsTable);
		recentAddedContentsTable.setWidth("100%");
		recentAddedContentsTable.setHeight("200px");
		pagelayout.add(recentAddedLayout);

		// Nedávno upravené obsahy
		VerticalLayout recentModifiedLayout = new VerticalLayout();
		recentModifiedLayout.setPadding(false);
		recentModifiedLayout.add(new H2("Nedávno upravené obsahy"));
		recentModifiedLayout.add(recentModifiedContentsTable);
		recentModifiedContentsTable.setWidth("100%");
		recentModifiedContentsTable.setHeight("200px");
		pagelayout.add(recentModifiedLayout);

	}

}
