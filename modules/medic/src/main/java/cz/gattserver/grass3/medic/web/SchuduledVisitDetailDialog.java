package cz.gattserver.grass3.medic.web;

import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;

import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.interfaces.ScheduledVisitTO;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.Strong;

public class SchuduledVisitDetailDialog extends Dialog {

	private static final long serialVersionUID = -1240133390770972624L;

	public SchuduledVisitDetailDialog(Long id) {
		final ScheduledVisitTO scheduledVisitDTO = SpringContextHelper.getBean(MedicFacade.class)
				.getScheduledVisitById(id);

		add(new Strong("Datum"));
		add(scheduledVisitDTO.getDate().atTime(scheduledVisitDTO.getTime())
				.format(DateTimeFormatter.ofPattern("d. MMMM yyyy, H:mm")));

		add(new Strong("Účel"));
		add(scheduledVisitDTO.getPurpose());

		add(new Strong("Instituce"));
		final Button instButton = new Button(scheduledVisitDTO.getInstitution().getName(),
				e -> new MedicalInstitutionDetailDialog(scheduledVisitDTO.getInstitution().getId()).open());
		instButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		add(instButton);

		add(new Strong("Navazuje na"));
		if (scheduledVisitDTO.getRecord() != null) {
			final Button recordButton = new Button(scheduledVisitDTO.getRecord().toString(),
					e -> new MedicalRecordDetailDialog(scheduledVisitDTO.getRecord().getId()).open());
			recordButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			add(recordButton);
		} else {
			add("-");
		}

		add(new Strong("Pravidelnost (měsíce)"));
		add(String.valueOf(scheduledVisitDTO.getPeriod()));

		add(new Strong("Stav"));
		add(String.valueOf(scheduledVisitDTO.getState()));
	}
}
