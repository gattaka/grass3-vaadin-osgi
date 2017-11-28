package cz.gattserver.grass3.ui.pages;

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;

import cz.gattserver.grass3.interfaces.QuoteTO;
import cz.gattserver.web.common.ui.FieldUtils;
import cz.gattserver.web.common.ui.window.WebWindow;

public class QuoteWindow extends WebWindow {

	private static final long serialVersionUID = -8494081277784752858L;

	interface SaveAction {
		void onSave(QuoteTO quoteDTO);
	}

	public QuoteWindow(SaveAction saveAction) {
		super("Nová hláška");
		init(null, saveAction);
	}

	public QuoteWindow(QuoteTO quote, SaveAction saveAction) {
		super("Upravit hlášku");
		init(quote, saveAction);
	}

	private void init(QuoteTO quote, SaveAction saveAction) {
		final int maxLength = 90;
		final TextArea newQuoteText = new TextArea();
		newQuoteText.setRows(2);
		newQuoteText.setMaxLength(maxLength);

		if (quote != null)
			newQuoteText.setValue(quote.getName());

		FieldUtils.addValidator(newQuoteText, new StringLengthValidator(
				"Text hlášky nesmí být prázdný a může mít maximálně " + maxLength + " znaků", 1, maxLength));
		newQuoteText.setWidth("400px");
		addComponent(newQuoteText);

		addComponent(new Button("Uložit", event -> {
			if (newQuoteText.getComponentError() != null)
				return;
			QuoteTO q = quote == null ? new QuoteTO() : quote;
			q.setName(newQuoteText.getValue());
			saveAction.onSave(q);
			close();
		}), Alignment.MIDDLE_CENTER);
	}

}
