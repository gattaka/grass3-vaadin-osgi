package cz.gattserver.grass3.language.web;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;

import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.ui.components.CreateButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.web.common.ui.window.WebWindow;

public class LanguageItemWindow extends WebWindow {

	private static final long serialVersionUID = 6803519662032576371L;

	interface SaveAction {
		void onSave(LanguageItemTO itemTO);
	}

	public LanguageItemWindow(SaveAction action) {
		this(null, action);
	}

	public LanguageItemWindow(LanguageItemTO to, SaveAction action) {
		super(to == null ? "Založit" : "Upravit" + " záznam");

		setWidth("600px");

		LanguageItemTO targetTO = to == null ? new LanguageItemTO() : to;

		Binder<LanguageItemTO> binder = new Binder<>();

		TextField contentField = new TextField("Obsah");
		contentField.setWidth("100%");
		binder.forField(contentField).asRequired().bind(LanguageItemTO::getContent, LanguageItemTO::setContent);
		addComponent(contentField);
		contentField.focus();

		TextField translationField = new TextField("Překlad");
		translationField.setWidth("100%");
		binder.forField(translationField).asRequired().bind(LanguageItemTO::getTranslation,
				LanguageItemTO::setTranslation);
		addComponent(translationField);

		translationField.addShortcutListener(new ShortcutListener("Submit", ShortcutAction.KeyCode.ENTER, null) {
			private static final long serialVersionUID = -7239845094514060176L;

			@Override
			public void handleAction(Object sender, Object target) {
				onSave(action, binder, targetTO);
			}
		});

		binder.readBean(targetTO);

		Button.ClickListener clickListener = e -> onSave(action, binder, targetTO);

		Button b;
		if (to != null)
			b = new ModifyButton(clickListener);
		else
			b = new CreateButton(clickListener);
		addComponent(b);
		setComponentAlignment(b, Alignment.MIDDLE_CENTER);
	}

	private void onSave(SaveAction action, Binder<LanguageItemTO> binder, LanguageItemTO targetTO) {
		if (binder.writeBeanIfValid(targetTO)) {
			action.onSave(targetTO);
			close();
		}
	}

}
