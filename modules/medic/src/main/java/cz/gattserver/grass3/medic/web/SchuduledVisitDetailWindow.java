package cz.gattserver.grass3.medic.web;

import java.time.format.DateTimeFormatter;

import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.ui.windows.DetailDialog;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.BoldSpan;

public class SchuduledVisitDetailWindow extends DetailDialog {

	private static final long serialVersionUID = -1240133390770972624L;

	public SchuduledVisitDetailWindow(Long id) {
		super("Detail návštěvy");

		final ScheduledVisitDTO scheduledVisitDTO = SpringContextHelper.getBean(MedicFacade.class)
				.getScheduledVisitById(id);

		GridLayout layout = new GridLayout(2, 6);
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.addComponent(new BoldSpan("Datum"));
		layout.addComponent(
				new Label(scheduledVisitDTO.getDate().format(DateTimeFormatter.ofPattern("d. MMMM yyyy, H:mm"))));
		layout.addComponent(new BoldSpan("Účel"));
		layout.addComponent(new Label(scheduledVisitDTO.getPurpose()));

		final Button instButton = new Button(scheduledVisitDTO.getInstitution().getName());
		instButton.addClickListener(e -> UI.getCurrent()
				.addWindow(new MedicalInstitutionDetailWindow(scheduledVisitDTO.getInstitution().getId())));
		instButton.setStyleName(ValoTheme.BUTTON_LINK);
		layout.addComponent(new BoldSpan("Instituce"));
		layout.addComponent(instButton);

		layout.addComponent(new BoldSpan("Navazuje na"));
		if (scheduledVisitDTO.getRecord() != null) {
			final Button recordButton = new Button(scheduledVisitDTO.getRecord().toString());
			recordButton.addClickListener(e -> UI.getCurrent()
					.addWindow(new MedicalRecordDetailWindow(scheduledVisitDTO.getRecord().getId())));
			recordButton.setStyleName(ValoTheme.BUTTON_LINK);
			layout.addComponent(recordButton);
		} else {
			layout.addComponent(new Label("-"));
		}

		layout.addComponent(new BoldSpan("Pravidelnost (měsíce)"));
		layout.addComponent(new Label(String.valueOf(scheduledVisitDTO.getPeriod())));

		layout.addComponent(new BoldSpan("Stav"));
		layout.addComponent(new Label(String.valueOf(scheduledVisitDTO.getState())));

		setContent(layout);

	}
}
