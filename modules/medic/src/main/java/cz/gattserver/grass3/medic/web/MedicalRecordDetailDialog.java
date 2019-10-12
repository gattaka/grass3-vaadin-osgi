package cz.gattserver.grass3.medic.web;

import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.interfaces.MedicalRecordTO;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.Strong;

public class MedicalRecordDetailDialog extends Dialog {

	private static final long serialVersionUID = -1240133390770972624L;

	private transient MedicFacade medicFacade;

	public MedicalRecordDetailDialog(Long id) {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setPadding(false);
		add(layout);

		setWidth("400px");
		
		MedicalRecordTO medicalRecordDTO = getMedicFacade().getMedicalRecordById(id);

		layout.add(new Strong("Datum"));
		layout.add(medicalRecordDTO.getDateTime().format(DateTimeFormatter.ofPattern("d. MMMM yyyy, H:mm")));

		layout.add(new Strong("Instituce"));
		final Button button = new Button(medicalRecordDTO.getInstitution().getName(),
				e -> new MedicalInstitutionDetailDialog(medicalRecordDTO.getInstitution().getId()).open());
		button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		button.addClassName("top-clean");
		layout.add(button);

		layout.add(new Strong("Ošetřující lékař"));
		layout.add(medicalRecordDTO.getPhysician().getName());

		layout.add(new Strong("Záznam"));
		Div div = new Div();
		div.setText(medicalRecordDTO.getRecord());
		div.setWidth("100%");
		div.getStyle().set("white-space", "pre-wrap");
		div.addClassName("top-clean");
		layout.add(div);
	}

	protected MedicFacade getMedicFacade() {
		if (medicFacade == null)
			medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		return medicFacade;
	}
}
