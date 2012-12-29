package org.myftp.gattserver.grass3.search;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.myftp.gattserver.grass3.search.service.SearchHit;
import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class SearchWindow extends OneColumnWindow {

	private static final long serialVersionUID = -7063551976547889914L;

	public static final String NAME = "search";

	private SearchFacade searchFacade = SearchFacade.INSTANCE;

	public SearchWindow() {
		setName(NAME);
	}

	@Override
	protected void createContent(VerticalLayout layout) {

		layout.setMargin(true);
		layout.setSpacing(true);

		HorizontalLayout searchLayout = new HorizontalLayout();
		layout.addComponent(searchLayout);
		searchLayout.setWidth("100%");
		searchLayout.setSpacing(true);

		final VerticalLayout outputLayout = new VerticalLayout();
		layout.addComponent(outputLayout);

		final TextField searchField = new TextField();
		searchField.setWidth("100%");
		searchLayout.addComponent(searchField);
		searchLayout.setExpandRatio(searchField, 1);

		Button searchButton = new Button("Hledat", new Button.ClickListener() {

			private static final long serialVersionUID = -9210575562255933575L;

			public void buttonClick(ClickEvent event) {

				String searchText = (String) searchField.getValue();
				try {
					List<SearchHit> hits = searchFacade
							.searchArticles(searchText);
					outputLayout.removeAllComponents();
					for (SearchHit hit : hits) {
						outputLayout.addComponent(new Label(hit
								.getContentLink()));
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		});
		searchLayout.addComponent(searchButton);

	}

}
