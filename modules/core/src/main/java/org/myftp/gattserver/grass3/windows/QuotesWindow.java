package org.myftp.gattserver.grass3.windows;

import org.myftp.gattserver.grass3.facades.QuotesFacade;
import org.myftp.gattserver.grass3.model.dto.QuoteDTO;
import org.myftp.gattserver.grass3.security.CoreACL;
import org.myftp.gattserver.grass3.template.InfoNotification;
import org.myftp.gattserver.grass3.template.WarningNotification;
import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class QuotesWindow extends OneColumnWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	private QuotesFacade quotesFacade = QuotesFacade.INSTANCE;

	public static final String NAME = "quotes";
	private Panel newQuotesPanel;

	/**
	 * Tabulka hlášek
	 */
	private Table table;

	public QuotesWindow() {
		setName(NAME);
		setCaption("Gattserver - Hlášky");
	}

	@Override
	protected void createContent(VerticalLayout layout) {

		layout.setMargin(true);
		layout.setSpacing(true);
		table = new Table();
		table.setSizeFull();
		layout.addComponent(table);

		createNewQuotePanel(layout);
		createQuoteList();

	}

	@Override
	protected void onShow() {
		createQuoteList();

		CoreACL acl = getUserACL();
		newQuotesPanel.setVisible(acl.canModifyQuotes());

		super.onShow();
	}

	private void createQuoteList() {

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
		newQuotesPanel = new Panel("Nová hláška");
		layout.addComponent(newQuotesPanel);

		HorizontalLayout panelBackgroudLayout = new HorizontalLayout();
		panelBackgroudLayout.setSizeFull();
		newQuotesPanel.setContent(panelBackgroudLayout);

		HorizontalLayout panelLayout = new HorizontalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setMargin(true);
		panelBackgroudLayout.addComponent(panelLayout);

		final int maxLength = 90;
		final TextField newQuoteText = new TextField();
		newQuoteText.setWidth("200px");
		newQuoteText.addValidator(new StringLengthValidator(
				"Text hlášky nesmí být prázdný a může mít maximálně "
						+ maxLength + " znaků", 1, maxLength, false));
		panelLayout.addComponent(newQuoteText);

		Button createButton = new Button("Vytvořit",
				new Button.ClickListener() {

					private static final long serialVersionUID = -4315617904120991885L;

					public void buttonClick(ClickEvent event) {
						if (newQuoteText.isValid() == false)
							return;

						if (quotesFacade.createNewQuote((String) newQuoteText
								.getValue())) {
							showNotification(new InfoNotification(
									"Nová hláška byla úspěšně vložena."));
							// refresh list
							createQuoteList();
							// clean
							newQuoteText.setValue("");
						} else {
							showNotification(new WarningNotification(
									"Nezdařilo se vložit novou hlášku."));
						}

					}
				});
		panelLayout.addComponent(createButton);

	}
}
