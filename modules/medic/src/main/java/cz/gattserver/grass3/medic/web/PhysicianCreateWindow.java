package cz.gattserver.grass3.medic.web;

import com.vaadin.ui.Button;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.medic.dto.PhysicianDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class PhysicianCreateWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	public PhysicianCreateWindow() {
		this(null);
	}

	public PhysicianCreateWindow(PhysicianDTO originalDTO) {
		super(originalDTO == null ? "Přidání doktora" : "Úprava doktora");

		GridLayout winLayout = new GridLayout(2, 3);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		winLayout.setWidth("300px");

		PhysicianDTO formDTO = new PhysicianDTO();
		Binder<PhysicianDTO> binder = new Binder<>(PhysicianDTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField("Jméno");
		winLayout.addComponent(nameField, 0, 0, 1, 0);
		nameField.setWidth("100%");
		binder.forField(nameField).bind("name");

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 1);

		Button saveBtn = new Button(originalDTO == null ? "Přidat" : "Upravit", e -> {
			try {
				PhysicianDTO writeDTO = originalDTO == null ? new PhysicianDTO() : originalDTO;
				binder.writeBean(writeDTO);
				SpringContextHelper.getBean(MedicFacade.class).savePhysician(formDTO);
				onSuccess();
				close();
			} catch (ValidationException ex) {
				Notification.show("   Chybná vstupní data\n\n   " + ex.getCause().getMessage(),
						Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception ex) {
				UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se vytvořit nový záznam"));
			}
		});
		winLayout.addComponent(saveBtn, 1, 2);
		winLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);

		if (originalDTO != null)
			binder.readBean(originalDTO);

		setContent(winLayout);
	}

	protected abstract void onSuccess();

}
