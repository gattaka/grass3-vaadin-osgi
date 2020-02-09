package cz.gattserver.grass3.language.web.dialogs;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.language.model.dto.LanguageTO;
import cz.gattserver.grass3.ui.components.SaveCloseLayout;
import cz.gattserver.web.common.ui.window.WebDialog;

public class LanguageDialog extends WebDialog {

	private static final long serialVersionUID = -8494081277784752858L;

	public interface SaveAction {
		void onSave(LanguageTO langTO);
	}

	public LanguageDialog(SaveAction saveAction) {
		init(null, saveAction);
	}

	public LanguageDialog(LanguageTO langTO, SaveAction saveAction) {
		init(langTO, saveAction);
	}

	private void init(LanguageTO to, SaveAction saveAction) {
		LanguageTO targetTO = to == null ? new LanguageTO() : to;

		setWidth("400px");

		Binder<LanguageTO> binder = new Binder<>();

		TextField nameField = new TextField();
		binder.forField(nameField).asRequired().bind(LanguageTO::getName, LanguageTO::setName);
		addComponent(nameField);

		binder.readBean(targetTO);

		add(new SaveCloseLayout(event -> {
			if (binder.writeBeanIfValid(targetTO)) {
				saveAction.onSave(targetTO);
				close();
			}
		}, e -> close()));
	}

}
