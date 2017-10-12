package cz.gattserver.grass3.medic.web;

import java.text.SimpleDateFormat;

import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.template.AbstractDetailSubWindow;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.ui.BoldLabel;

public class SchuduledVisitDetailWindow extends AbstractDetailSubWindow {

	private static final long serialVersionUID = -1240133390770972624L;

	private MedicFacade medicalFacade;

	public SchuduledVisitDetailWindow(Long id) {
		super("Detail návštěvy");

		medicalFacade = SpringContextHelper.getBean(MedicFacade.class);

		final ScheduledVisitDTO scheduledVisitDTO = medicalFacade.getScheduledVisitById(id);

		SimpleDateFormat dateFormat = new SimpleDateFormat("d. MMMMM yyyy, H:mm");

		GridLayout layout = new GridLayout(2, 6);
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.addComponent(new BoldLabel("Datum"));
		layout.addComponent(new Label(dateFormat.format(scheduledVisitDTO.getDate())));
		layout.addComponent(new BoldLabel("Účel"));
		layout.addComponent(new Label(scheduledVisitDTO.getPurpose()));

		final Button instButton = new Button(scheduledVisitDTO.getInstitution().getName());
		instButton.addClickListener(e -> UI.getCurrent()
				.addWindow(new MedicalInstitutionDetailWindow(scheduledVisitDTO.getInstitution().getId())));
		instButton.setStyleName(ValoTheme.BUTTON_LINK);
		layout.addComponent(new BoldLabel("Instituce"));
		layout.addComponent(instButton);

		layout.addComponent(new BoldLabel("Navazuje na"));
		if (scheduledVisitDTO.getRecord() != null) {
			final Button recordButton = new Button(scheduledVisitDTO.getRecord().toString());
			recordButton.addClickListener(e -> UI.getCurrent()
					.addWindow(new MedicalRecordDetailWindow(scheduledVisitDTO.getRecord().getId())));
			recordButton.setStyleName(ValoTheme.BUTTON_LINK);
			layout.addComponent(recordButton);
		} else {
			layout.addComponent(new Label("-"));
		}

		layout.addComponent(new BoldLabel("Pravidelnost (měsíce)"));
		layout.addComponent(new Label(String.valueOf(scheduledVisitDTO.getPeriod())));

		layout.addComponent(new BoldLabel("Stav"));
		layout.addComponent(new Label(String.valueOf(scheduledVisitDTO.getState())));

		setContent(layout);

	}
}
