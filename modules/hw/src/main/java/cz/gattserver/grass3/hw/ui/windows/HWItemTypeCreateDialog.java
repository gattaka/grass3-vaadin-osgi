package cz.gattserver.grass3.hw.ui.windows;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.ui.components.SaveCloseButtons;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class HWItemTypeCreateDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient HWService hwService;

	public HWItemTypeCreateDialog(HWItemTypeTO originalDTO) {
		init(originalDTO);
	}

	public HWItemTypeCreateDialog() {
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
		binder.bind(nameField, "name");

		add(nameField);

		SaveCloseButtons buttons = new SaveCloseButtons(e -> {
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
		add(buttons);

		if (originalDTO != null)
			binder.readBean(originalDTO);
	}

	protected abstract void onSuccess(HWItemTypeTO hwItemTypeDTO);

}
