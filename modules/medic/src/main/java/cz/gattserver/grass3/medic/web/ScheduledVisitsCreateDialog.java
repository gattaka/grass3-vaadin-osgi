package cz.gattserver.grass3.medic.web;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import cz.gattserver.grass3.medic.dto.MedicalRecordDTO;
import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.dto.ScheduledVisitState;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.ui.components.SaveCloseButtons;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class ScheduledVisitsCreateDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private static final Logger logger = LoggerFactory.getLogger(ScheduledVisitsCreateDialog.class);

	enum Operation {
		TO_BE_PLANNED, PLANNED, PLANNED_FROM_TO_BE_PLANNED
	}

	public ScheduledVisitsCreateDialog(Operation operation) {
		this(operation, null);
	}

	public ScheduledVisitsCreateDialog(Operation operation, ScheduledVisitDTO originalDTO) {
		boolean planned = operation.equals(Operation.PLANNED) || operation.equals(Operation.PLANNED_FROM_TO_BE_PLANNED);

		MedicFacade medicalFacade = SpringContextHelper.getBean(MedicFacade.class);

		ScheduledVisitDTO formDTO = new ScheduledVisitDTO();
		formDTO.setPurpose("");
		formDTO.setPlanned(planned);
		formDTO.setState(planned ? ScheduledVisitState.PLANNED : ScheduledVisitState.TO_BE_PLANNED);

		Binder<ScheduledVisitDTO> binder = new Binder<>(ScheduledVisitDTO.class);
		binder.setBean(formDTO);

		final TextField purposeField = new TextField("Účel návštěvy");
		add(purposeField);
		purposeField.setWidth("100%");
		binder.forField(purposeField).asRequired().bind("purpose");

		if (!planned) {
			final TextField periodField = new TextField("Pravidelnost (měsíce)");
			add(periodField);
			periodField.setWidth("100%");
			binder.forField(periodField)
					.withConverter(new StringToIntegerConverter(0, "Počet měsíců musí být celé číslo")).bind("period");
		}

		final DatePicker dateField = new DatePicker("Datum návštěvy");
		dateField.setLocale(Locale.forLanguageTag("CS"));
		add(dateField);
		dateField.setWidth("100%");
		binder.forField(dateField).bind("date");

		if (planned) {
			final TimePicker timeField = new TimePicker("Čas návštěvy");
			timeField.setLocale(Locale.forLanguageTag("CS"));
			add(timeField);
			timeField.setWidth("100%");
			binder.forField(timeField).bind("time");
		}

		dateField.setWidth("100%");
		binder.forField(dateField).asRequired().bind("date");

		List<MedicalRecordDTO> records = medicalFacade.getAllMedicalRecords();
		final ComboBox<MedicalRecordDTO> recordsComboBox = new ComboBox<>("Navazuje na kontrolu", records);
		add(recordsComboBox);
		recordsComboBox.setWidth("100%");
		binder.forField(recordsComboBox).bind("record");

		List<MedicalInstitutionDTO> institutions = medicalFacade.getAllMedicalInstitutions();
		final ComboBox<MedicalInstitutionDTO> institutionComboBox = new ComboBox<>("Instituce", institutions);
		add(institutionComboBox);
		institutionComboBox.setWidth("100%");
		binder.forField(institutionComboBox).asRequired().bind("institution");

		add(new SaveCloseButtons(e -> {
			ScheduledVisitDTO writeDTO = originalDTO == null ? formDTO : originalDTO;
			if (binder.writeBeanIfValid(writeDTO)) {
				try {
					medicalFacade.saveScheduledVisit(writeDTO);
					onSuccess();
					close();
				} catch (Exception ex) {
					String msg = "Nezdařilo se vytvořit nový záznam";
					new ErrorDialog(msg).open();
					logger.error(msg, ex);
				}
			}
		}, e -> close()));

		if (originalDTO != null)
			binder.readBean(originalDTO);

		// vyplňuji objednání na základě plánovaného objednání
		if (originalDTO != null && planned) {
			purposeField.setValue(originalDTO.getPurpose());
			recordsComboBox.setValue(originalDTO.getRecord());
			institutionComboBox.setValue(originalDTO.getInstitution());
		}
	}

	protected abstract void onSuccess();

}
