package cz.gattserver.grass3.medic.web;

import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;

import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.interfaces.MedicalRecordTO;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.WebDialog;

public class MedicalRecordDetailDialog extends WebDialog {

	private static final long serialVersionUID = -1240133390770972624L;

	private transient MedicFacade medicFacade;

	public MedicalRecordDetailDialog(Long id) {
		super("Detail záznamu");

		MedicalRecordTO medicalRecordDTO = getMedicFacade().getMedicalRecordById(id);

		add(new H2("Datum"));
		add(medicalRecordDTO.getDate().format(DateTimeFormatter.ofPattern("d. MMMMM yyyy, H:mm")));

		add(new H2("Instituce"));
		final Button button = new Button(medicalRecordDTO.getInstitution().getName(),
				e -> new MedicalInstitutionDetailDialog(medicalRecordDTO.getInstitution().getId()).open());
		button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		add(button);

		add(new H2("Ošetřující lékař"));
		add(medicalRecordDTO.getPhysician().getName());

		add(new H2("Záznam"));
		Div div = new Div();
		div.setText(medicalRecordDTO.getRecord());
		div.setWidth("600px");
		div.getStyle().set("white-space", "pre");
		add(div);
	}

	protected MedicFacade getMedicFacade() {
		if (medicFacade == null)
			medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		return medicFacade;
	}
}
