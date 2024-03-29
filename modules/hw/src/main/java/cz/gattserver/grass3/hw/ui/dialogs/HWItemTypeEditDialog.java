package cz.gattserver.grass3.hw.ui.dialogs;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.ui.components.SaveCloseLayout;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.EditWebDialog;
import cz.gattserver.web.common.ui.window.ErrorDialog;

public abstract class HWItemTypeEditDialog extends EditWebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient HWService hwService;

	public HWItemTypeEditDialog(HWItemTypeTO originalDTO) {
		init(originalDTO);
	}

	public HWItemTypeEditDialog() {
		init(null);
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	public void init(HWItemTypeTO originalDTO) {
		HWItemTypeTO formDTO = new HWItemTypeTO();
		formDTO.setName("");
		Binder<HWItemTypeTO> binder = new Binder<>(HWItemTypeTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField();
		nameField.setPlaceholder("Typ HW");
		nameField.setWidthFull();
		binder.forField(nameField).asRequired().bind(HWItemTypeTO::getName, HWItemTypeTO::setName);

		add(nameField);

		SaveCloseLayout buttons = new SaveCloseLayout(e -> {
			try {
				HWItemTypeTO writeDTO = originalDTO == null ? new HWItemTypeTO() : originalDTO;
				binder.writeBean(writeDTO);
				getHWService().saveHWType(writeDTO);
				onSuccess(writeDTO);
				close();
			} catch (Exception ex) {
				new ErrorDialog("Uložení se nezdařilo").open();
			}
		}, e -> close());
		buttons.setMinWidth("200px");
		buttons.getSaveButton().addClickShortcut(Key.ENTER);
		add(buttons);

		if (originalDTO != null)
			binder.readBean(originalDTO);

		nameField.focus();
	}

	protected abstract void onSuccess(HWItemTypeTO hwItemTypeDTO);

}
