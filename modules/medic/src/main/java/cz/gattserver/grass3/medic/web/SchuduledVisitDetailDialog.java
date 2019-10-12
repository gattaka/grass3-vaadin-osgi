package cz.gattserver.grass3.medic.web;

import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.interfaces.ScheduledVisitTO;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.Strong;

public class SchuduledVisitDetailDialog extends Dialog {

	private static final long serialVersionUID = -1240133390770972624L;

	public SchuduledVisitDetailDialog(Long id) {
		final ScheduledVisitTO scheduledVisitDTO = SpringContextHelper.getBean(MedicFacade.class)
				.getScheduledVisitById(id);

		setWidth("400px");

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setPadding(false);
		add(layout);

		layout.add(new Strong("Datum"));
		layout.add(scheduledVisitDTO.getDate().atTime(scheduledVisitDTO.getTime())
				.format(DateTimeFormatter.ofPattern("d. MMMM yyyy, H:mm")));

		layout.add(new Strong("Účel"));
		layout.add(scheduledVisitDTO.getPurpose());

		layout.add(new Strong("Instituce"));
		final Button instButton = new Button(scheduledVisitDTO.getInstitution().getName(),
				e -> new MedicalInstitutionDetailDialog(scheduledVisitDTO.getInstitution().getId()).open());
		instButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		instButton.addClassName("top-clean");
		layout.add(instButton);

		layout.add(new Strong("Navazuje na"));
		if (scheduledVisitDTO.getRecord() != null) {
			final Button recordButton = new Button(scheduledVisitDTO.getRecord().toString(),
					e -> new MedicalRecordDetailDialog(scheduledVisitDTO.getRecord().getId()).open());
			recordButton.addClassName("top-clean");
			recordButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			layout.add(recordButton);
		} else {
			layout.add("-");
		}

		layout.add(new Strong("Pravidelnost (měsíce)"));
		layout.add(String.valueOf(scheduledVisitDTO.getPeriod()));

		layout.add(new Strong("Stav"));
		layout.add(String.valueOf(scheduledVisitDTO.getState()));
	}
}
