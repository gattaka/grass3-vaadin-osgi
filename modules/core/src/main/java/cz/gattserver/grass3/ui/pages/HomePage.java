package cz.gattserver.grass3.ui.pages;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass3.interfaces.ContentTagsCloudItemTO;
import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.ContentsLazyGrid;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.ui.HtmlSpan;

@Route(value = "")
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

	@Override
	protected Component createColumnContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setPadding(false);
		layout.setSpacing(true);
		layout.setSizeFull();

		// Oblíbené
		UserInfoTO user = getUser();
		if (coreACL.isLoggedIn(user)) {
			layout.add(new H2("Oblíbené obsahy"));
			ContentsLazyGrid favouritesContentsTable = new ContentsLazyGrid();
			favouritesContentsTable.populate(getUser().getId() != null, this,
					q -> contentNodeFacade.getUserFavourite(user.getId(), q.getOffset(), q.getLimit()).stream(),
					q -> contentNodeFacade.getUserFavouriteCount(user.getId()));
			layout.add(favouritesContentsTable);
			favouritesContentsTable.setWidth("100%");
		}

		layout.add(createSearchMenu());

		// Nedávno přidané a upravené obsahy
		layout.add(createRecentAdded());
		layout.add(createRecentModified());

		// Tag-cloud
		Div tagJsDiv = new Div() {
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void tagCloundCallback() {
				layout.add(createTagCloud());
			}
		};

		String tagJsDivId = "tag-js-div";
		tagJsDiv.setId(tagJsDivId);
		layout.add(tagJsDiv);

		UI.getCurrent().getPage().executeJs("setTimeout(function(){ document.getElementById('" + tagJsDivId
				+ "').$server.tagCloundCallback() }, 10);");

		return layout;
	}

	private Component createSearchMenu() {
		Div searchResultsLayout = new Div();
		searchResultsLayout.setWidthFull();
		searchResultsLayout.add(new H2("Vyhledávání"));

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
				searchResultsTable.populate(getUser().getId() != null, HomePage.this,
						q -> contentNodeFacade
								.getByName(searchField.getValue(), user.getId(), q.getOffset(), q.getLimit()).stream(),
						q -> contentNodeFacade.getCountByName(searchField.getValue(), user.getId()));
				searchResultsTable.setHeight("200px");
			}
			searchResultsTable.getDataProvider().refreshAll();

		});
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.setValueChangeTimeout(200);

		return searchResultsLayout;
	}

	private Component createTagCloud() {
		Div tagCloudLayout = new Div();
		tagCloudLayout.add(new H2("Tagy"));

		List<ContentTagsCloudItemTO> contentTags = contentTagFacade.createTagsCloud(MAX_FONT_SIZE_TAG_CLOUD,
				MIN_FONT_SIZE_TAG_CLOUD);
		if (contentTags.isEmpty()) {
			Span noTagsSpan = new Span("Nebyly nalezeny žádné tagy");
			tagCloudLayout.add(noTagsSpan);
			return tagCloudLayout;
		}

		char oldChar = 0;
		char currChar = 0;
		StringBuilder sb = null;
		for (ContentTagsCloudItemTO contentTag : contentTags) {
			currChar = contentTag.getName().toUpperCase().charAt(0);
			if (currChar != oldChar || oldChar == 0) {
				if (oldChar != 0) {
					populateTags(sb, oldChar, tagCloudLayout);
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
			populateTags(sb, currChar, tagCloudLayout);

		return tagCloudLayout;
	}

	private void populateTags(StringBuilder sb, char tag, Div tagCloudLayout) {
		Div tagBlock = new Div();
		tagCloudLayout.add(tagBlock);

		Div tagLetter = new Div();
		tagLetter.addClassName("tag-letter");
		tagLetter.add(String.valueOf(tag));
		tagBlock.add(tagLetter);
		tagLetter.setSizeUndefined();

		Div tagLabels = new Div();
		tagLabels.addClassName("tag-labels");
		tagBlock.add(tagLabels);
		
		Span tags = new HtmlSpan(sb.toString());
		tagLabels.add(tags);
		tags.setSizeFull();
	}

	private Component createRecentAdded() {
		ContentsLazyGrid recentAddedContentsTable = new ContentsLazyGrid();
		recentAddedContentsTable.populate(getUser().getId() != null, this,
				q -> contentNodeFacade.getRecentAdded(q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeFacade.getCount());

		Div recentAddedLayout = new Div();
		recentAddedLayout.setWidthFull();
		recentAddedLayout.add(new H2("Nedávno přidané obsahy"));
		recentAddedLayout.add(recentAddedContentsTable);
		recentAddedContentsTable.setWidth("100%");
		recentAddedContentsTable.setHeight("200px");

		return recentAddedLayout;
	}

	private Component createRecentModified() {
		ContentsLazyGrid recentModifiedContentsTable = new ContentsLazyGrid();
		recentModifiedContentsTable.populate(getUser().getId() != null, this,
				q -> contentNodeFacade.getRecentModified(q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeFacade.getCount());

		Div recentModifiedLayout = new Div();
		recentModifiedLayout.setWidthFull();
		recentModifiedLayout.add(new H2("Nedávno upravené obsahy"));
		recentModifiedLayout.add(recentModifiedContentsTable);
		recentModifiedContentsTable.setWidth("100%");
		recentModifiedContentsTable.setHeight("200px");

		return recentModifiedLayout;
	}

}
