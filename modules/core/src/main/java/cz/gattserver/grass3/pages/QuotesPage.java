package cz.gattserver.grass3.pages;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.ui.Button.ClickEvent;

import cz.gattserver.grass3.facades.QuotesFacade;
import cz.gattserver.grass3.model.dto.QuoteDTO;
import cz.gattserver.grass3.pages.template.OneColumnPage;
import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.ui.FieldUtils;

public class QuotesPage extends OneColumnPage {

	private static final long serialVersionUID = 2474374292329895766L;

	@Autowired
	private CoreACL coreACL;

	@Autowired
	private QuotesFacade quotesFacade;

	public QuotesPage(GrassRequest request) {
		super(request);
	}

	/**
	 * Seznam hlášek
	 */
	private Table table;

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		createQuoteList(layout);
		createNewQuotePanel(layout);

		return layout;
	}

	private void createQuoteList(VerticalLayout layout) {

		table = new Table();
		table.setSizeFull();
		layout.addComponent(table);

		populateQuotesTable();
	}

	private void populateQuotesTable() {

		IndexedContainer container = new IndexedContainer();
		container.addContainerProperty("ID", Long.class, 1L);
		container.addContainerProperty("Obsah", String.class, "");

		table.setContainerDataSource(container);

		for (QuoteDTO quote : quotesFacade.getAllQuotes()) {
			Item item = table.addItem(quote);
			item.getItemProperty("ID").setValue(quote.getId());
			item.getItemProperty("Obsah").setValue(quote.getName());
		}

	}

	private void createNewQuotePanel(VerticalLayout layout) {

		Panel newQuotesPanel = new Panel("Nová hláška");
		layout.addComponent(newQuotesPanel);

		newQuotesPanel.setVisible(coreACL.canModifyQuotes(getUser()));

		HorizontalLayout panelBackgroudLayout = new HorizontalLayout();
		panelBackgroudLayout.setSizeFull();
		newQuotesPanel.setContent(panelBackgroudLayout);

		HorizontalLayout panelLayout = new HorizontalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setMargin(true);
		panelBackgroudLayout.addComponent(panelLayout);

		final int maxLength = 90;
		final TextField newQuoteText = new TextField();
		FieldUtils.addValidator(newQuoteText, new StringLengthValidator(
				"Text hlášky nesmí být prázdný a může mít maximálně " + maxLength + " znaků", 1, maxLength));
		newQuoteText.setWidth("200px");
		panelLayout.addComponent(newQuoteText);

		Button createButton = new Button("Vytvořit", new Button.ClickListener() {

			private static final long serialVersionUID = -4315617904120991885L;

			public void buttonClick(ClickEvent event) {
				// TODO
				if (newQuoteText.getComponentError() != null)
					return;

				if (quotesFacade.createNewQuote(newQuoteText.getValue())) {
					showInfo("Nová hláška byla úspěšně vložena.");
					// refresh list
					populateQuotesTable();
					// clean
					newQuoteText.setValue("");
				} else {
					showWarning("Nezdařilo se vložit novou hlášku.");
				}

			}
		});
		panelLayout.addComponent(createButton);

	}
}