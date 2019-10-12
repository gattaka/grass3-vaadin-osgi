package cz.gattserver.grass3.medic.web;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import cz.gattserver.grass3.medic.dto.MedicalRecordDTO;
import cz.gattserver.grass3.medic.dto.MedicamentDTO;
import cz.gattserver.grass3.medic.dto.PhysicianDTO;
import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.ui.components.SaveCloseButtons;
import cz.gattserver.grass3.ui.util.TokenField;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class MedicalRecordCreateDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient MedicFacade medicFacade;

	public MedicalRecordCreateDialog() {
		this(null, null);
	}

	public MedicalRecordCreateDialog(ScheduledVisitDTO scheduledVisitDTO) {
		this(scheduledVisitDTO, null);
	}

	public MedicalRecordCreateDialog(MedicalRecordDTO recordDTO) {
		this(null, recordDTO);
	}

	private MedicalRecordCreateDialog(ScheduledVisitDTO scheduledVisitDTO, MedicalRecordDTO originalDTO) {
		setWidth("400px");

		MedicalRecordDTO formDTO = new MedicalRecordDTO();

		Binder<MedicalRecordDTO> binder = new Binder<>(MedicalRecordDTO.class);
		binder.setBean(formDTO);

		Set<PhysicianDTO> physicians = getMedicFacade().getAllPhysicians();
		final ComboBox<PhysicianDTO> physicianComboBox = new ComboBox<>("Ošetřující lékař", physicians);
		add(physicianComboBox);
		physicianComboBox.setWidth("100%");
		binder.forField(physicianComboBox).bind("physician");

		final DatePicker dateField = new DatePicker("Datum návštěvy");
		dateField.setLocale(Locale.forLanguageTag("CS"));
		add(dateField);
		dateField.setWidth("100%");
		binder.forField(dateField).bind("date");
		
		final TimePicker timeField = new TimePicker("Čas návštěvy");
		timeField.setLocale(Locale.forLanguageTag("CS"));
		add(timeField);
		timeField.setWidth("100%");
		binder.forField(timeField).bind("time");

		final ComboBox<MedicalInstitutionDTO> institutionComboBox = new ComboBox<>("Instituce",
				getMedicFacade().getAllMedicalInstitutions());
		add(institutionComboBox);
		institutionComboBox.setWidth("100%");
		binder.forField(institutionComboBox).bind("institution");

		final TextArea recordField = new TextArea("Záznam");
		add(recordField);
		recordField.setWidth("100%");
		binder.forField(recordField).bind("record");

		Map<String, MedicamentDTO> medicaments = new HashMap<String, MedicamentDTO>();
		for (MedicamentDTO mto : getMedicFacade().getAllMedicaments())
			medicaments.put(mto.getName(), mto);

		TokenField tokenField = new TokenField(medicaments.keySet());
		tokenField.setPlaceholder("Medikament");
		add(tokenField);

		add(new SaveCloseButtons(e -> {
			MedicalRecordDTO writeDTO = originalDTO == null ? new MedicalRecordDTO() : originalDTO;
			if (binder.writeBeanIfValid(writeDTO)) {
				try {
					writeDTO.setMedicaments(
							tokenField.getValues().stream().map(medicaments::get).collect(Collectors.toSet()));
					getMedicFacade().saveMedicalRecord(writeDTO);
					onSuccess();
					close();
				} catch (Exception ex) {
					new ErrorDialog("Nezdařilo se uložit nový záznam").open();
				}
			}
		}, e -> close()));

		if (originalDTO != null)
			binder.readBean(originalDTO);

		if (scheduledVisitDTO != null) {
			dateField.setValue(scheduledVisitDTO.getDate());
			timeField.setValue(scheduledVisitDTO.getTime());
			institutionComboBox.setValue(scheduledVisitDTO.getInstitution());
		}
	}

	protected MedicFacade getMedicFacade() {
		if (medicFacade == null)
			medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		return medicFacade;
	}

	protected abstract void onSuccess();

}
