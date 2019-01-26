package cz.gattserver.grass3.language.web;

import java.util.Arrays;

import com.vaadin.data.Binder;
import com.vaadin.data.Validator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.language.model.domain.ItemType;
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.ui.components.CreateButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.web.common.ui.window.ConfirmWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public class LanguageItemWindow extends WebWindow {

	private static final long serialVersionUID = 6803519662032576371L;

	interface SaveAction {
		void onSave(LanguageItemTO itemTO);
	}

	public LanguageItemWindow(SaveAction action, Validator<String> validator, ItemType asType) {
		this(null, action, validator, asType);
	}

	public LanguageItemWindow(LanguageItemTO to, SaveAction action, Validator<String> validator, ItemType asType) {
		super(to == null ? "Založit" : "Upravit" + " záznam");

		removeCloseShortcut(KeyCode.ESCAPE);

		setWidth("600px");

		if (asType == null)
			asType = ItemType.values()[0];

		LanguageItemTO targetTO = to == null ? new LanguageItemTO() : to;
		targetTO.setType(asType);

		Binder<LanguageItemTO> binder = new Binder<>();

		RadioButtonGroup<ItemType> typeRadio = new RadioButtonGroup<>(null, Arrays.asList(ItemType.values()));
		typeRadio.setItemCaptionGenerator(ItemType::getCaption);
		binder.forField(typeRadio).bind(LanguageItemTO::getType, LanguageItemTO::setType);
		typeRadio.setStyleName("horizontal");
		addComponent(typeRadio);

		TextField contentField = new TextField("Obsah");
		contentField.setWidth("100%");
		binder.forField(contentField).asRequired().withValidator(validator).bind(LanguageItemTO::getContent,
				LanguageItemTO::setContent);
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

		HorizontalLayout buttonLayout = new HorizontalLayout();
		addComponent(buttonLayout);
		setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

		if (to != null) {
			buttonLayout.addComponent(new ModifyButton(e -> onSave(action, binder, targetTO)));
		} else {
			buttonLayout.addComponent(new CreateButton(e -> onSave(action, binder, targetTO)));
			Button createAndContinueBtn = new CreateButton(
					e -> onSaveAndContinue(action, binder, targetTO, validator, typeRadio.getValue()));
			createAndContinueBtn.setCaption("Vytvořit a pokračovat");
			buttonLayout.addComponent(createAndContinueBtn);
		}
	}

	private void onSave(SaveAction action, Binder<LanguageItemTO> binder, LanguageItemTO targetTO) {
		if (binder.writeBeanIfValid(targetTO)) {
			checkAndThen(targetTO, () -> {
				action.onSave(targetTO);
				close();
			});
		}
	}

	private void onSaveAndContinue(SaveAction action, Binder<LanguageItemTO> binder, LanguageItemTO targetTO,
			Validator<String> validator, ItemType asType) {
		if (binder.writeBeanIfValid(targetTO)) {
			checkAndThen(targetTO, () -> {
				action.onSave(targetTO);
				UI.getCurrent().addWindow(new LanguageItemWindow(action, validator, asType));
				close();
			});
		}
	}

	private void checkAndThen(LanguageItemTO targetTO, Runnable r) {
		if (targetTO.getContent().split(" ").length > 2 && ItemType.WORD.equals(targetTO.getType())) {
			UI.getCurrent().addWindow(new ConfirmWindow(
					"Opravdu uložit slovní spojení jako '" + ItemType.WORD.getCaption() + "' ?", e -> {
						r.run();
					}));
		} else if (targetTO.getContent().split(" ").length == 1 && ItemType.PHRASE.equals(targetTO.getType())) {
			UI.getCurrent().addWindow(
					new ConfirmWindow("Opravdu uložit jedno slovo '" + ItemType.PHRASE.getCaption() + "' ?", e -> {
						r.run();
					}));
		} else {
			r.run();
		}
	}

}
