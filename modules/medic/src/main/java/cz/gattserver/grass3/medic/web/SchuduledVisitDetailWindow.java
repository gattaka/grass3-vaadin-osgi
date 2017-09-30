package cz.gattserver.grass3.medic.web;

import java.text.SimpleDateFormat;

import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.themes.BaseTheme;
import com.vaadin.ui.Button.ClickEvent;

import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.template.AbstractDetailSubWindow;
import cz.gattserver.web.common.SpringContextHelper;

public class SchuduledVisitDetailWindow extends AbstractDetailSubWindow {

	private static final long serialVersionUID = -1240133390770972624L;

	private MedicFacade medicalFacade;

	public SchuduledVisitDetailWindow(Long id) {
		super("Detail návštěvy");

		medicalFacade = SpringContextHelper.getBean(MedicFacade.class);

		final ScheduledVisitDTO scheduledVisitDTO = medicalFacade.getScheduledVisitById(id);

		SimpleDateFormat dateFormat = new SimpleDateFormat("d. MMMMM yyyy, H:mm");
		addDetailLine("Datum", dateFormat.format(scheduledVisitDTO.getDate()));

		addDetailLine("Účel", scheduledVisitDTO.getPurpose());

		final Button instButton = new Button(scheduledVisitDTO.getInstitution().getName());
		instButton.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 4609212946079293192L;

			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent()
						.addWindow(new MedicalInstitutionDetailWindow(scheduledVisitDTO.getInstitution().getId()));
			}
		});
		instButton.setStyleName(BaseTheme.BUTTON_LINK);
		addDetailLine("Instituce", instButton);

		if (scheduledVisitDTO.getRecord() != null) {
			final Button recordButton = new Button(scheduledVisitDTO.getRecord().toString());
			recordButton.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 4609212946079293192L;

				@Override
				public void buttonClick(ClickEvent event) {
					UI.getCurrent().addWindow(new MedicalRecordDetailWindow(scheduledVisitDTO.getRecord().getId()));
				}
			});
			recordButton.setStyleName(BaseTheme.BUTTON_LINK);
			addDetailLine("Navazuje na", recordButton);
		} else {
			addDetailLine("Navazuje na", "-");
		}

		addDetailLine("Pravidelnost (měsíce)", String.valueOf(scheduledVisitDTO.getPeriod()));

		addDetailLine("Stav", String.valueOf(scheduledVisitDTO.getState()));

		setContent(layout);

	}
}
