package cz.gattserver.grass3.medic.web;

import java.time.format.DateTimeFormatter;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Button.ClickEvent;

import cz.gattserver.grass3.medic.dto.MedicalRecordDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.ui.windows.DetailDialog;
import cz.gattserver.web.common.spring.SpringContextHelper;

public class MedicalRecordDetailWindow extends DetailDialog {

	private static final long serialVersionUID = -1240133390770972624L;

	private transient MedicFacade medicFacade;

	public MedicalRecordDetailWindow(Long id) {
		super("Detail záznamu");

		MedicalRecordDTO medicalRecordDTO = getMedicFacade().getMedicalRecordById(id);

		addDetailLine("Datum", medicalRecordDTO.getDate().format(DateTimeFormatter.ofPattern("d. MMMMM yyyy, H:mm")));

		final Button button = new Button(medicalRecordDTO.getInstitution().getName());
		button.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 4609212946079293192L;

			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent()
						.addWindow(new MedicalInstitutionDetailWindow(medicalRecordDTO.getInstitution().getId()));
			}
		});
		button.setStyleName(ValoTheme.BUTTON_LINK);
		addDetailLine("Instituce", button);

		addDetailLine("Ošetřující lékař", medicalRecordDTO.getPhysician().getName());

		Label label;
		label = addDetailLine("Záznam", medicalRecordDTO.getRecord());
		label.setWidth("600px");
	}

	protected MedicFacade getMedicFacade() {
		if (medicFacade == null)
			medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		return medicFacade;
	}
}
