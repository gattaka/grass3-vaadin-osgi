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

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class MedicalInstitutionCreateWindow extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient MedicFacade medicFacade;

	public MedicalInstitutionCreateWindow() {
		this(null);
	}

	public MedicalInstitutionCreateWindow(MedicalInstitutionDTO modifiedMedicalInstitutionDTO) {
		super(modifiedMedicalInstitutionDTO == null ? "Založení nové instituce" : "Úprava instituce");

		GridLayout winLayout = new GridLayout(2, 6);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		winLayout.setWidth("500px");

		MedicalInstitutionDTO formDTO = new MedicalInstitutionDTO();
		Binder<MedicalInstitutionDTO> binder = new Binder<>(MedicalInstitutionDTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField("Název");
		winLayout.addComponent(nameField, 0, 0, 1, 0);
		nameField.setWidth("100%");
		binder.forField(nameField).bind("name");

		final TextField addressField = new TextField("Adresa");
		winLayout.addComponent(addressField, 0, 1, 1, 1);
		addressField.setWidth("100%");
		binder.forField(addressField).bind("address");

		final TextField webField = new TextField("Webové stránky");
		winLayout.addComponent(webField, 0, 2, 1, 2);
		webField.setWidth("100%");
		binder.forField(webField).bind("web");

		final TextArea hoursField = new TextArea("Otevírací hodiny");
		winLayout.addComponent(hoursField, 0, 3, 1, 3);
		hoursField.setWidth("100%");
		hoursField.setHeight("200px");
		binder.forField(hoursField).bind("hours");

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 4);

		Button saveBtn = new Button(modifiedMedicalInstitutionDTO == null ? "Založit" : "Upravit", e -> {
			try {
				MedicalInstitutionDTO writeDTO = modifiedMedicalInstitutionDTO == null ? new MedicalInstitutionDTO()
						: modifiedMedicalInstitutionDTO;
				binder.writeBean(writeDTO);
				getMedicFacade().saveMedicalInstitution(writeDTO);
				onSuccess();
				close();
			} catch (ValidationException ex) {
				Notification.show("   Chybná vstupní data\n\n   " + ex.getCause().getMessage(),
						Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception ex) {
				UI.getCurrent().addWindow(new ErrorDialog("Nezdařilo se vytvořit nový záznam"));
			}
		});
		winLayout.addComponent(saveBtn, 1, 5);
		winLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);

		if (modifiedMedicalInstitutionDTO != null)
			binder.readBean(modifiedMedicalInstitutionDTO);

		setContent(winLayout);
	}

	protected MedicFacade getMedicFacade() {
		if (medicFacade == null)
			medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		return medicFacade;
	}

	protected abstract void onSuccess();

}
