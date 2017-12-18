package cz.gattserver.grass3.fm.web;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import cz.gattserver.web.common.ui.window.WebWindow;

public class FileNameWindow extends WebWindow {

	private static final long serialVersionUID = 9163906666470561249L;

	interface SaveAction {
		void onSave(String name, Window w);
	}

	public FileNameWindow(String title, SaveAction saveAction) {
		this(title, null, saveAction);
	}

	public FileNameWindow(String title, String defaultValue, SaveAction saveAction) {
		super(title);
		final TextField nameField = new TextField();
		nameField.setWidth("400px");
		addComponent(nameField);
		final Button btn = new Button("Uložit", event -> {
			if (nameField.getComponentError() != null)
				return;
			saveAction.onSave(nameField.getValue(), FileNameWindow.this);
			close();
		});
		btn.setEnabled(false);
		addComponent(btn, Alignment.MIDDLE_CENTER);

		nameField.addValueChangeListener(e -> btn.setEnabled(StringUtils.isNotBlank(e.getValue())));
		if (defaultValue != null)
			nameField.setValue(defaultValue);
	}
}
