package cz.gattserver.grass3.medic.web;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass3.ui.components.SaveCloseLayout;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class MedicalInstitutionCreateDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient MedicFacade medicFacade;

	public MedicalInstitutionCreateDialog() {
		this(null);
	}

	public MedicalInstitutionCreateDialog(MedicalInstitutionTO modifiedMedicalInstitutionDTO) {
		setWidth("500px");

		MedicalInstitutionTO formDTO = new MedicalInstitutionTO();
		Binder<MedicalInstitutionTO> binder = new Binder<>(MedicalInstitutionTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField("Název");
		add(nameField);
		nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
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

		add(new SaveCloseLayout(e -> {
			MedicalInstitutionTO writeDTO = modifiedMedicalInstitutionDTO == null ? new MedicalInstitutionTO()
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
