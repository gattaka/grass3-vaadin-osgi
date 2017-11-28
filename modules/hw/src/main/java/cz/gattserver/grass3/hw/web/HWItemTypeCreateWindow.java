package cz.gattserver.grass3.hw.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.facade.HWFacade;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class HWItemTypeCreateWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	@Autowired
	private HWFacade hwFacade;

	public HWItemTypeCreateWindow(Long originalId) {
		if (originalId == null) {
			init(null);
		} else {
			HWItemTypeDTO originalDTO = hwFacade.getHWItemType(originalId);
			init(originalDTO);
		}
	}

	public HWItemTypeCreateWindow(HWItemTypeDTO originalDTO) {
		init(originalDTO);
	}

	public HWItemTypeCreateWindow() {
		init(null);
	}

	public void init(HWItemTypeDTO originalDTO) {
		setCaption("Založení nového typu HW");

		VerticalLayout winLayout = new VerticalLayout();
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		HWItemTypeDTO formDTO = new HWItemTypeDTO();
		formDTO.setName("");

		final TextField nameField = new TextField();
		Binder<HWItemTypeDTO> binder = new Binder<HWItemTypeDTO>(HWItemTypeDTO.class);
		binder.setBean(formDTO);
		binder.bind(nameField, "name");

		winLayout.addComponent(nameField);
		winLayout.addComponent(new Button("Uložit", e -> {
			try {
				HWItemTypeDTO writeDTO = originalDTO == null ? new HWItemTypeDTO() : originalDTO;
				binder.writeBean(writeDTO);
				hwFacade.saveHWType(writeDTO);
				onSuccess(writeDTO);
				close();
			} catch (ValidationException ex) {
				Notification.show("   Chybná vstupní data\n\n   " + ex.getBeanValidationErrors().iterator().next(),
						Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception ex) {
				UI.getCurrent().addWindow(new ErrorWindow("Uložení se nezdařilo"));
			}
		}));

		if (originalDTO != null)
			binder.readBean(originalDTO);

		setContent(winLayout);
	}

	protected abstract void onSuccess(HWItemTypeDTO hwItemTypeDTO);

}
