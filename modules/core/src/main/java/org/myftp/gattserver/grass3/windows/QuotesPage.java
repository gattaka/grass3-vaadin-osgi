package org.myftp.gattserver.grass3.windows;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.QuotesFacade;
import org.myftp.gattserver.grass3.model.dto.QuoteDTO;
import org.myftp.gattserver.grass3.security.CoreACL;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.template.GrassPage;
import org.myftp.gattserver.grass3.windows.template.OneColumnPage;
import org.myftp.gattserver.grass3.windows.template.PageFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class QuotesPage extends OneColumnPage {

	private static final long serialVersionUID = 2474374292329895766L;

	@Resource(name = "quotesFacade")
	QuotesFacade quotesFacade;

	public static final PageFactory FACTORY = new PageFactory("quotes") {
		@Override
		public GrassPage createPage(GrassRequest request) {
			return new QuotesPage(request);
		}
	};

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

		CoreACL acl = getUserACL();
		newQuotesPanel.setVisible(acl.canModifyQuotes());

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
