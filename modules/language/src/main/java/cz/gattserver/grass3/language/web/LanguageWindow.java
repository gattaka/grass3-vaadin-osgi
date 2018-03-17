package cz.gattserver.grass3.language.web;

import com.vaadin.data.Binder;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;

import cz.gattserver.grass3.language.model.dto.LanguageTO;
import cz.gattserver.web.common.ui.window.WebWindow;

public class LanguageWindow extends WebWindow {

	private static final long serialVersionUID = -8494081277784752858L;

	interface SaveAction {
		void onSave(LanguageTO langTO);
	}

	public LanguageWindow(SaveAction saveAction) {
		super("Nový jazyk");
		init(null, saveAction);
	}

	public LanguageWindow(LanguageTO langTO, SaveAction saveAction) {
		super("Upravit jazyk");
		init(langTO, saveAction);
	}

	private void init(LanguageTO to, SaveAction saveAction) {
		LanguageTO targetTO = to == null ? new LanguageTO() : to;

		Binder<LanguageTO> binder = new Binder<>();

		TextField nameField = new TextField();
		binder.forField(nameField).asRequired().bind(LanguageTO::getName, LanguageTO::setName);
		addComponent(nameField);

		binder.readBean(targetTO);

		addComponent(new Button("Uložit", event -> {
			if (binder.writeBeanIfValid(targetTO)) {
				saveAction.onSave(targetTO);
				close();
			}
		}), Alignment.MIDDLE_CENTER);
	}

}
