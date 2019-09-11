package cz.gattserver.grass3.ui.pages;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;

import cz.gattserver.grass3.interfaces.QuoteTO;
import cz.gattserver.web.common.ui.window.WebDialog;

public class QuoteDialog extends WebDialog {

	private static final long serialVersionUID = -8494081277784752858L;

	interface SaveAction {
		void onSave(QuoteTO quoteDTO);
	}

	public QuoteDialog(SaveAction saveAction) {
		super("Nová hláška");
		init(null, saveAction);
	}

	public QuoteDialog(QuoteTO quote, SaveAction saveAction) {
		super("Upravit hlášku");
		init(quote, saveAction);
	}

	private void init(QuoteTO quote, SaveAction saveAction) {
		final int maxLength = 90;
		final TextArea newQuoteText = new TextArea();
		newQuoteText.setMaxLength(maxLength);

		if (quote != null)
			newQuoteText.setValue(quote.getName());

		final Binder<QuoteTO> binder = new Binder<>();
		binder.setBean(new QuoteTO());
		if (quote != null)
			binder.readBean(quote);
		binder.forField(newQuoteText)
				.withValidator(new StringLengthValidator(
						"Text hlášky nesmí být prázdný a může mít maximálně " + maxLength + " znaků", 1, maxLength))
				.bind(QuoteTO::getName, QuoteTO::setName);
		newQuoteText.setWidth("400px");
		addComponent(newQuoteText);

		addComponent(new Button("Uložit", event -> {
			if (!binder.validate().isOk())
				return;
			QuoteTO q = binder.getBean();
			q.setName(newQuoteText.getValue());
			saveAction.onSave(q);
			close();
		}));
	}

}
