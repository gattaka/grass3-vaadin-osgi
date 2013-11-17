package org.myftp.gattserver.grass3.medic.web;

import java.text.SimpleDateFormat;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.template.AbstractDetailSubWindow;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

public class SchuduledVisitDetailWindow extends AbstractDetailSubWindow {

	private static final long serialVersionUID = -1240133390770972624L;

	private IMedicFacade medicalFacade;

	public SchuduledVisitDetailWindow(Long id,
			final Component... triggerComponent) {
		super("Detail návštěvy", triggerComponent);

		medicalFacade = SpringContextHelper.getBean(IMedicFacade.class);

		final ScheduledVisitDTO scheduledVisitDTO = medicalFacade
				.getScheduledVisitById(id);

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"d. MMMMM yyyy, H:mm");
		addDetailLine("Datum", dateFormat.format(scheduledVisitDTO.getDate()));

		addDetailLine("Účel", scheduledVisitDTO.getPurpose());

		final Button instButton = new Button(scheduledVisitDTO.getInstitution()
				.getName());
		instButton.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 4609212946079293192L;

			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().addWindow(
						new MedicalInstitutionDetailWindow(scheduledVisitDTO
								.getInstitution().getId(), instButton));
			}
		});
		instButton.setStyleName(BaseTheme.BUTTON_LINK);
		addDetailLine("Instituce", instButton);

		if (scheduledVisitDTO.getRecord() != null) {
			final Button recordButton = new Button(scheduledVisitDTO
					.getRecord().toString());
			recordButton.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 4609212946079293192L;

				@Override
				public void buttonClick(ClickEvent event) {
					UI.getCurrent().addWindow(
							new MedicalRecordDetailWindow(scheduledVisitDTO
									.getRecord().getId(), recordButton));
				}
			});
			recordButton.setStyleName(BaseTheme.BUTTON_LINK);
			addDetailLine("Navazuje na", recordButton);
		} else {
			addDetailLine("Navazuje na", "-");
		}

		addDetailLine("Pravidelnost (měsíce)",
				String.valueOf(scheduledVisitDTO.getPeriod()));

		addDetailLine("Stav", String.valueOf(scheduledVisitDTO.getState()));

		setContent(layout);

	}
}
