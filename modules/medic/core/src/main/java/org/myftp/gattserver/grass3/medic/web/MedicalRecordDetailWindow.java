package org.myftp.gattserver.grass3.medic.web;

import java.text.SimpleDateFormat;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.medic.dto.MedicalRecordDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.template.AbstractDetailSubWindow;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

public class MedicalRecordDetailWindow extends AbstractDetailSubWindow {

	private static final long serialVersionUID = -1240133390770972624L;

	private IMedicFacade medicalFacade;

	public MedicalRecordDetailWindow(Long id,
			final Component... triggerComponent) {
		super("Detail záznamu", triggerComponent);

		medicalFacade = SpringContextHelper.getBean(IMedicFacade.class);

		final MedicalRecordDTO medicalRecordDTO = medicalFacade
				.getMedicalRecordById(id);

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"d. MMMMM yyyy, H:mm");
		addDetailLine("Datum", dateFormat.format(medicalRecordDTO.getDate()));

		final Button button = new Button(medicalRecordDTO.getInstitution()
				.getName());
		button.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 4609212946079293192L;

			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().addWindow(
						new MedicalInstitutionDetailWindow(medicalRecordDTO
								.getInstitution().getId(), button));
			}
		});
		button.setStyleName(BaseTheme.BUTTON_LINK);
		addDetailLine("Instituce", button);

		addDetailLine("Ošetřující lékař", medicalRecordDTO.getPhysician()
				.getName());

		Label label;
		label = addDetailLine("Záznam", medicalRecordDTO.getRecord());
		label.setContentMode(ContentMode.PREFORMATTED);

		setContent(layout);

	}
}
