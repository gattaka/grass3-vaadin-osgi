package cz.gattserver.grass3.medic.web;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.ui.components.SaveCloseButtons;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class MedicalInstitutionCreateDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient MedicFacade medicFacade;

	public MedicalInstitutionCreateDialog() {
		this(null);
	}

	public MedicalInstitutionCreateDialog(MedicalInstitutionDTO modifiedMedicalInstitutionDTO) {
		setWidth("500px");

		MedicalInstitutionDTO formDTO = new MedicalInstitutionDTO();
		Binder<MedicalInstitutionDTO> binder = new Binder<>(MedicalInstitutionDTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField("Název");
		add(nameField);
		nameField.setWidth("100%");
		binder.forField(nameField).bind("name");

		final TextField addressField = new TextField("Adresa");
		add(addressField);
		addressField.setWidth("100%");
		binder.forField(addressField).bind("address");

		final TextField webField = new TextField("Webové stránky");
		add(webField);
		webField.setWidth("100%");
		binder.forField(webField).bind("web");

		final TextArea hoursField = new TextArea("Otevírací hodiny");
		add(hoursField);
		hoursField.setWidth("100%");
		hoursField.setHeight("200px");
		binder.forField(hoursField).bind("hours");

		add(new SaveCloseButtons(e -> {
			MedicalInstitutionDTO writeDTO = modifiedMedicalInstitutionDTO == null ? new MedicalInstitutionDTO()
					: modifiedMedicalInstitutionDTO;
			if (binder.writeBeanIfValid(writeDTO)) {
				try {
					getMedicFacade().saveMedicalInstitution(writeDTO);
					onSuccess();
					close();
				} catch (Exception ex) {
					new ErrorDialog("Nezdařilo se vytvořit nový záznam").open();
				}
			}
		}, e -> close()));

		if (modifiedMedicalInstitutionDTO != null)
			binder.readBean(modifiedMedicalInstitutionDTO);
	}

	protected MedicFacade getMedicFacade() {
		if (medicFacade == null)
			medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		return medicFacade;
	}

	protected abstract void onSuccess();

}
