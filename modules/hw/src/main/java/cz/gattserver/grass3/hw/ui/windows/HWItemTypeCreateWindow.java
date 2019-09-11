package cz.gattserver.grass3.hw.ui.windows;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class HWItemTypeCreateWindow extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient HWService hwService;

	public HWItemTypeCreateWindow(HWItemTypeTO originalDTO) {
		init(originalDTO);
	}

	public HWItemTypeCreateWindow() {
		init(null);
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}


	public void init(HWItemTypeTO originalDTO) {
		setCaption("Založení nového typu HW");

		VerticalLayout winLayout = new VerticalLayout();
		winLayout.setPadding(true);
		winLayout.setSpacing(true);

		HWItemTypeTO formDTO = new HWItemTypeTO();
		formDTO.setName("");
		Binder<HWItemTypeTO> binder = new Binder<>(HWItemTypeTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField();
		binder.bind(nameField, "name");

		winLayout.addComponent(nameField);
		winLayout.addComponent(new Button("Uložit", e -> {
			try {
				HWItemTypeTO writeDTO = originalDTO == null ? new HWItemTypeTO() : originalDTO;
				binder.writeBean(writeDTO);
				getHWService().saveHWType(writeDTO);
				onSuccess(writeDTO);
				close();
			} catch (ValidationException ex) {
				Notification.show("   Chybná vstupní data\n\n   " + ex.getBeanValidationErrors().iterator().next(),
						Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception ex) {
				UI.getCurrent().addWindow(new ErrorDialog("Uložení se nezdařilo"));
			}
		}));

		if (originalDTO != null)
			binder.readBean(originalDTO);

		setContent(winLayout);
		
		removeAllCloseShortcuts();
	}

	protected abstract void onSuccess(HWItemTypeTO hwItemTypeDTO);

}
