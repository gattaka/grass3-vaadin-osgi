package org.myftp.gattserver.grass3.search;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.myftp.gattserver.grass3.pages.template.OneColumnPage;
import org.myftp.gattserver.grass3.search.service.SearchHit;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class SearchWindow extends OneColumnPage {

	private static final long serialVersionUID = -7063551976547889914L;

	public static final String NAME = "search";

	private SearchFacade searchFacade = SearchFacade.INSTANCE;

	private VerticalLayout layout;
	private final VerticalLayout outputLayout = new VerticalLayout();
	private final TextField searchField = new TextField();
	private final ComboBox moduleCombo = new ComboBox();

	public SearchWindow() {
		setName(NAME);
	}

	private void searchAndPrintHits() {

		String searchText = (String) searchField.getValue();
		try {
			List<SearchHit> hits = searchFacade.search(searchText, null,
					(String) moduleCombo.getValue(),
					getApplication().getUser(), SearchWindow.this);
			outputLayout.removeAllComponents();

			if (hits.size() == 0) {
				outputLayout.addComponent(new Label("Na dotaz '" + searchText
						+ "' nebyly nalezeny žádné záznamy."));
			} else {
				for (SearchHit hit : hits) {
					String link = hit.getContentLink();
					VerticalLayout hitLayout = new VerticalLayout();
					outputLayout.addComponent(hitLayout);
					hitLayout.addComponent(new Link(link, new ExternalResource(
							link)));
					Label highlightLabel = new Label(hit.getHitFieldText(),
							Label.CONTENT_XHTML);
					hitLayout.addComponent(highlightLabel);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onShow() {
		super.onShow();

		layout.removeAllComponents();

		Set<String> moduleIds = searchFacade.getSearchModulesIds();
		if (moduleIds == null || moduleIds.isEmpty()) {
			layout.addComponent(new Label(
					"Nebyly nalezeny žádné moduly, ve kterých by se dalo vyhledávat."));
			return;
		}

		HorizontalLayout searchLayout = new HorizontalLayout();
		layout.addComponent(searchLayout);
		searchLayout.setWidth("100%");
		searchLayout.setSpacing(true);

		outputLayout.setSpacing(true);
		outputLayout.removeAllComponents();
		layout.addComponent(outputLayout);

		/**
		 * 1.) Tlačítko
		 */
		Button searchButton = new Button("Hledat", new Button.ClickListener() {

			private static final long serialVersionUID = -9210575562255933575L;

			public void buttonClick(ClickEvent event) {
				searchAndPrintHits();
			}
		});
		searchLayout.addComponent(searchButton);
		searchButton.setIcon(new ThemeResource("img/tags/search_16.png"));

		/**
		 * 2.) Rozbalovací seznam
		 */
		for (String moduleId : moduleIds) {
			moduleCombo.addItem(moduleId);
		}
		moduleCombo.setNullSelectionAllowed(false);
		moduleCombo.setValue(moduleIds.iterator().next());
		moduleCombo.setFilteringMode(Filtering.FILTERINGMODE_OFF);
		moduleCombo.setImmediate(true);
		searchLayout.addComponent(moduleCombo);

		/**
		 * 3.) Pole s dotazem, které je protažené až do konce panelu
		 */
		searchLayout.addComponent(searchField);
		searchLayout.setExpandRatio(searchField, 1);
		searchField.setWidth("100%");
		searchField.setValue("");

		// při od-enterování by se mělo provést vyhledání
		searchField.addShortcutListener(new ShortcutListener("Shortcut Name",
				ShortcutAction.KeyCode.ENTER, null) {

			private static final long serialVersionUID = 4399065369299557562L;

			@Override
			public void handleAction(Object sender, Object target) {
				searchAndPrintHits();
			}
		});

		// zaměř se na textové pole
		searchField.focus();

	}

	@Override
	protected void createContent(VerticalLayout layout) {

		layout.setMargin(true);
		layout.setSpacing(true);
		this.layout = layout;

	}

}
