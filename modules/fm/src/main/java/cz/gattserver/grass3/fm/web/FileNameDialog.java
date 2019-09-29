package cz.gattserver.grass3.fm.web;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.fm.interfaces.FMItemTO;
import cz.gattserver.grass3.ui.components.button.SaveButton;
import cz.gattserver.web.common.ui.window.WebDialog;

public class FileNameDialog extends WebDialog {

	private static final long serialVersionUID = 9163906666470561249L;

	public interface SaveAction {
		void onSave(FMItemTO quoteDTO, FileNameDialog dialog);
	}

	public FileNameDialog(SaveAction saveAction) {
		init(null, saveAction);
	}

	public FileNameDialog(FMItemTO quote, SaveAction saveAction) {
		init(quote, saveAction);
	}

	private void init(FMItemTO existingTO, SaveAction saveAction) {
		final TextField textField = new TextField();
		if (existingTO != null)
			textField.setValue(existingTO.getName());

		final Binder<FMItemTO> binder = new Binder<>();
		binder.setBean(new FMItemTO());
		if (existingTO != null)
			binder.readBean(existingTO);
		binder.forField(textField).asRequired().bind(FMItemTO::getName, FMItemTO::setName);
		textField.setWidth("400px");
		addComponent(textField);

		addComponent(new SaveButton(event -> {
			if (!binder.validate().isOk())
				return;
			FMItemTO to = binder.getBean();
			to.setName(textField.getValue());
			saveAction.onSave(to, this);
		}));
	}
}
