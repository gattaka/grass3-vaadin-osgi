package cz.gattserver.grass3.medic.web;

import java.util.Locale;
import java.util.Set;

import com.vaadin.ui.Button;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import cz.gattserver.grass3.medic.dto.MedicalRecordDTO;
import cz.gattserver.grass3.medic.dto.MedicamentDTO;
import cz.gattserver.grass3.medic.dto.PhysicianDTO;
import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class MedicalRecordCreateWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private MedicFacade medicalFacade;

	public MedicalRecordCreateWindow() {
		this(null, null);
	}

	public MedicalRecordCreateWindow(ScheduledVisitDTO scheduledVisitDTO) {
		this(scheduledVisitDTO, null);
	}

	public MedicalRecordCreateWindow(MedicalRecordDTO recordDTO) {
		this(null, recordDTO);
	}

	private MedicalRecordCreateWindow(ScheduledVisitDTO scheduledVisitDTO, MedicalRecordDTO originalDTO) {
		super(originalDTO == null ? "Založení nového záznamu" : "Úprava záznamu");

		medicalFacade = SpringContextHelper.getBean(MedicFacade.class);

		GridLayout winLayout = new GridLayout(2, 7);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		winLayout.setWidth("400px");

		MedicalRecordDTO formDTO = new MedicalRecordDTO();

		Binder<MedicalRecordDTO> binder = new Binder<MedicalRecordDTO>(MedicalRecordDTO.class);
		binder.setBean(formDTO);

		Set<PhysicianDTO> physicians = medicalFacade.getAllPhysicians();
		final ComboBox<PhysicianDTO> physicianComboBox = new ComboBox<>("Ošetřující lékař", physicians);
		winLayout.addComponent(physicianComboBox, 0, 0, 1, 0);
		physicianComboBox.setWidth("100%");
		physicianComboBox.setEmptySelectionAllowed(false);
		binder.forField(physicianComboBox).bind("physician");

		final DateTimeField dateField = new DateTimeField("Datum návštěvy");
		dateField.setDateFormat("d. MMMMM yyyy, HH:mm");
		dateField.setLocale(Locale.forLanguageTag("CS"));
		dateField.setResolution(DateTimeResolution.MINUTE);
		winLayout.addComponent(dateField, 0, 1, 1, 1);
		dateField.setWidth("100%");
		binder.forField(dateField).bind("date");

		final ComboBox<MedicalInstitutionDTO> institutionComboBox = new ComboBox<>("Instituce",
				medicalFacade.getAllMedicalInstitutions());
		winLayout.addComponent(institutionComboBox, 0, 2, 1, 2);
		institutionComboBox.setWidth("100%");
		institutionComboBox.setEmptySelectionAllowed(false);
		binder.forField(institutionComboBox).bind("institution");

		final TextArea recordField = new TextArea("Záznam");
		winLayout.addComponent(recordField, 0, 3, 1, 3);
		recordField.setWidth("100%");
		binder.forField(recordField).bind("record");

		final TwinColSelect<MedicamentDTO> typeSelect = new TwinColSelect<>("Medikamenty",
				medicalFacade.getAllMedicaments());
		typeSelect.setWidth("100%");
		typeSelect.setRows(7);
		typeSelect.setItemCaptionGenerator(MedicamentDTO::getName);
		binder.forField(typeSelect).bind("medicaments");
		winLayout.addComponent(typeSelect, 0, 4, 1, 4);

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 5);

		Button saveBtn;
		winLayout.addComponent(saveBtn = new Button(originalDTO == null ? "Založit" : "Upravit", e -> {
			try {
				MedicalRecordDTO writeDTO = originalDTO == null ? new MedicalRecordDTO() : originalDTO;
				binder.writeBean(writeDTO);
				medicalFacade.saveMedicalRecord(writeDTO);
				onSuccess();
				close();
			} catch (ValidationException ex) {
				Notification.show("   Chybná vstupní data\n\n   " + ex.getCause().getMessage(),
						Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception ex) {
				UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se uložit nový záznam"));
			}
		}), 1, 6);
		winLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);

		if (originalDTO != null)
			binder.readBean(originalDTO);

		if (scheduledVisitDTO != null) {
			dateField.setValue(scheduledVisitDTO.getDate());
			institutionComboBox.setValue(scheduledVisitDTO.getInstitution());
		}

		setContent(winLayout);
	}

	protected abstract void onSuccess();

}
