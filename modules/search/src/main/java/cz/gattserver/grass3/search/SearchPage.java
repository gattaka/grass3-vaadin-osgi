package cz.gattserver.grass3.search;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.search.service.SearchHit;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.ImageIcons;

public class SearchPage extends OneColumnPage {

	public static final String NAME = "search";

	@Autowired
	private SearchFacade searchFacade;

	private VerticalLayout outputLayout;
	private TextField searchField;
	private ComboBox<String> moduleCombo;

	public SearchPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Layout createPayload() {
		outputLayout = new VerticalLayout();
		searchField = new TextField();
		moduleCombo = new ComboBox<>();
		return super.createPayload();
	}

	private void searchAndPrintHits() {

		String searchText = (String) searchField.getValue();
		try {
			List<SearchHit> hits = searchFacade.search(searchText, null, (String) moduleCombo.getValue(),
					UIUtils.getGrassUI().getUser(), this);
			outputLayout.removeAllComponents();

			if (hits.size() == 0) {
				outputLayout.addComponent(new Label("Na dotaz '" + searchText + "' nebyly nalezeny žádné záznamy."));
			} else {
				for (SearchHit hit : hits) {
					String link = hit.getContentLink();
					VerticalLayout hitLayout = new VerticalLayout();
					outputLayout.addComponent(hitLayout);
					hitLayout.addComponent(new Link(link, new ExternalResource(link)));
					Label highlightLabel = new Label(hit.getHitFieldText(), ContentMode.HTML);
					hitLayout.addComponent(highlightLabel);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			outputLayout.addComponent(new Label("Při vyhledávání došlo k chybě."));
		}
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		init(layout);
		return layout;
	}

	private void init(VerticalLayout layout) {

		Set<String> moduleIds = searchFacade.getSearchModulesIds();
		if (moduleIds == null || moduleIds.isEmpty()) {
			layout.addComponent(new Label("Nebyly nalezeny žádné moduly, ve kterých by se dalo vyhledávat."));
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
		searchButton.setIcon(new ThemeResource(ImageIcons.SEARCH_16_ICON));

		/**
		 * 2.) Rozbalovací seznam
		 */
		moduleCombo.setItems(moduleIds);
		moduleCombo.setEmptySelectionAllowed(false);
		moduleCombo.setValue(moduleIds.iterator().next());
		// TODO
		// moduleCombo.setFilteringMode(FilteringMode.CONTAINS);
		searchLayout.addComponent(moduleCombo);

		/**
		 * 3.) Pole s dotazem, které je protažené až do konce panelu
		 */
		searchLayout.addComponent(searchField);
		searchLayout.setExpandRatio(searchField, 1);
		searchField.setWidth("100%");
		searchField.setValue("");

		// při od-enterování by se mělo provést vyhledání
		searchField.addShortcutListener(new ShortcutListener("Shortcut Name", ShortcutAction.KeyCode.ENTER, null) {
			private static final long serialVersionUID = 4399065369299557562L;

			@Override
			public void handleAction(Object sender, Object target) {
				searchAndPrintHits();
			}
		});

		// zaměř se na textové pole
		searchField.focus();
	}

}
