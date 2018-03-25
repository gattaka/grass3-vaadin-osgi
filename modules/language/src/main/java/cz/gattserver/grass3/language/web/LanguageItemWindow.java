package cz.gattserver.grass3.language.web;

import java.util.Arrays;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.language.model.domain.ItemType;
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.ui.components.CreateButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.web.common.ui.window.WebWindow;

public class LanguageItemWindow extends WebWindow {

	private static final long serialVersionUID = 6803519662032576371L;

	interface SaveAction {
		void onSave(LanguageItemTO itemTO);
	}

	public LanguageItemWindow(SaveAction action, ItemType asType) {
		this(null, action, asType);
	}

	public LanguageItemWindow(LanguageItemTO to, SaveAction action, ItemType asType) {
		super(to == null ? "Založit" : "Upravit" + " záznam");

		setWidth("600px");

		LanguageItemTO targetTO = to == null ? new LanguageItemTO() : to;

		if (asType != null)
			targetTO.setType(asType);

		Binder<LanguageItemTO> binder = new Binder<>();

		ComboBox<ItemType> typeCombo = new ComboBox<>("Typ", Arrays.asList(ItemType.values()));
		typeCombo.setItemCaptionGenerator(ItemType::getCaption);
		binder.forField(typeCombo).asRequired().bind(LanguageItemTO::getType, LanguageItemTO::setType);
		typeCombo.setEmptySelectionAllowed(false);
		addComponent(typeCombo);

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

		ShortcutListener sl = new ShortcutListener("Submit", ShortcutAction.KeyCode.ENTER, null) {
			private static final long serialVersionUID = -7239845094514060176L;

			@Override
			public void handleAction(Object sender, Object target) {
				onSave(action, binder, targetTO);
			}
		};

		contentField.addShortcutListener(sl);
		translationField.addShortcutListener(sl);

		binder.readBean(targetTO);

		Button.ClickListener clickListener = e -> onSave(action, binder, targetTO);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		addComponent(buttonLayout);
		setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

		if (to != null) {
			buttonLayout.addComponent(new ModifyButton(clickListener));
		} else {
			buttonLayout.addComponent(new CreateButton(clickListener));
			Button createAndContinueBtn = new CreateButton(e -> {
				clickListener.buttonClick(e);
				UI.getCurrent().addWindow(new LanguageItemWindow(action, asType));
			});
			createAndContinueBtn.setCaption("Vytvořit a pokračovat");
			buttonLayout.addComponent(createAndContinueBtn);
		}
	}

	private void onSave(SaveAction action, Binder<LanguageItemTO> binder, LanguageItemTO targetTO) {
		if (binder.writeBeanIfValid(targetTO)) {
			action.onSave(targetTO);
			close();
		}
	}

}
