package cz.gattserver.grass3.medic.web;

import java.text.SimpleDateFormat;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.themes.BaseTheme;
import com.vaadin.ui.Button.ClickEvent;

import cz.gattserver.grass3.medic.dto.MedicalRecordDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.template.AbstractDetailSubWindow;
import cz.gattserver.web.common.SpringContextHelper;

public class MedicalRecordDetailWindow extends AbstractDetailSubWindow {

	private static final long serialVersionUID = -1240133390770972624L;

	private MedicFacade medicalFacade;

	public MedicalRecordDetailWindow(Long id) {
		super("Detail záznamu");

		medicalFacade = SpringContextHelper.getBean(MedicFacade.class);

		final MedicalRecordDTO medicalRecordDTO = medicalFacade.getMedicalRecordById(id);

		SimpleDateFormat dateFormat = new SimpleDateFormat("d. MMMMM yyyy, H:mm");
		addDetailLine("Datum", dateFormat.format(medicalRecordDTO.getDate()));

		final Button button = new Button(medicalRecordDTO.getInstitution().getName());
		button.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 4609212946079293192L;

			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent()
						.addWindow(new MedicalInstitutionDetailWindow(medicalRecordDTO.getInstitution().getId()));
			}
		});
		button.setStyleName(BaseTheme.BUTTON_LINK);
		addDetailLine("Instituce", button);

		addDetailLine("Ošetřující lékař", medicalRecordDTO.getPhysician().getName());

		Label label;
		label = addDetailLine("Záznam", medicalRecordDTO.getRecord());
		label.setContentMode(ContentMode.PREFORMATTED);

		setContent(layout);

	}
}
