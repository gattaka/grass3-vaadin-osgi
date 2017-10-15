package cz.gattserver.grass3.medic.web;

import com.vaadin.ui.Button;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.medic.dto.MedicamentDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.window.ErrorWindow;
import cz.gattserver.web.common.window.WebWindow;

public abstract class MedicamentCreateWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private MedicFacade medicalFacade;

	public MedicamentCreateWindow() {
		this(null);
	}

	public MedicamentCreateWindow(MedicamentDTO originalDTO) {
		super(originalDTO == null ? "Založení nového medikamentu" : "Úprava medikamentu");

		medicalFacade = SpringContextHelper.getBean(MedicFacade.class);

		GridLayout winLayout = new GridLayout(2, 4);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		winLayout.setWidth("300px");

		MedicamentDTO formDTO = new MedicamentDTO();
		Binder<MedicamentDTO> binder = new Binder<>(MedicamentDTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField("Název");
		winLayout.addComponent(nameField, 0, 0, 1, 0);
		nameField.setWidth("100%");
		binder.forField(nameField).bind("name");

		final TextArea toleranceField = new TextArea("Reakce, nežádoucí účinky");
		winLayout.addComponent(toleranceField, 0, 1, 1, 1);
		toleranceField.setWidth("100%");
		binder.forField(toleranceField).bind("tolerance");

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 2);

		Button saveBtn;
		winLayout.addComponent(saveBtn = new Button(originalDTO == null ? "Založit" : "Upravit", e -> {
			try {
				MedicamentDTO writeDTO = originalDTO == null ? new MedicamentDTO() : originalDTO;
				binder.writeBean(writeDTO);
				medicalFacade.saveMedicament(writeDTO);
				onSuccess();
				close();
			} catch (ValidationException ex) {
				Notification.show("   Chybná vstupní data\n\n   " + ex.getCause().getMessage(),
						Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception ex) {
				UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se vytvořit nový záznam"));
			}
		}), 1, 3);
		winLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);

		if (originalDTO != null)
			binder.readBean(originalDTO);

		setContent(winLayout);

	}

	protected abstract void onSuccess();

}
