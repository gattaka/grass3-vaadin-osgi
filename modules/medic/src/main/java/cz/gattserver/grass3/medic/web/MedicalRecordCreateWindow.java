package cz.gattserver.grass3.medic.web;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.shared.ui.datefield.Resolution;

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import cz.gattserver.grass3.medic.dto.MedicalRecordDTO;
import cz.gattserver.grass3.medic.dto.MedicamentDTO;
import cz.gattserver.grass3.medic.dto.PhysicianDTO;
import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.window.ErrorWindow;
import cz.gattserver.web.common.window.WebWindow;

public abstract class MedicalRecordCreateWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private MedicFacade medicalFacade;

	public MedicalRecordCreateWindow(final Component triggerComponent) {
		this(triggerComponent, null, null);
	}

	public MedicalRecordCreateWindow(final Component triggerComponent, ScheduledVisitDTO scheduledVisitDTO) {
		this(triggerComponent, scheduledVisitDTO, null);
	}

	public MedicalRecordCreateWindow(final Component triggerComponent, MedicalRecordDTO recordDTO) {
		this(triggerComponent, null, recordDTO);
	}

	private MedicalRecordCreateWindow(final Component triggerComponent, ScheduledVisitDTO scheduledVisitDTO,
			MedicalRecordDTO recordDTO) {
		super(recordDTO == null ? "Založení nového záznamu" : "Úprava záznamu");

		medicalFacade = SpringContextHelper.getBean(MedicFacade.class);

		triggerComponent.setEnabled(false);

		GridLayout winLayout = new GridLayout(2, 7);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		winLayout.setWidth("400px");

		final MedicalRecordDTO medicalRecordDTO = recordDTO == null ? new MedicalRecordDTO() : recordDTO;

		if (scheduledVisitDTO != null) {
			medicalRecordDTO.setDate(scheduledVisitDTO.getDate());
			medicalRecordDTO.setInstitution(scheduledVisitDTO.getInstitution());
		}

		final BeanFieldGroup<MedicalRecordDTO> fieldGroup = new BeanFieldGroup<MedicalRecordDTO>(
				MedicalRecordDTO.class);
		fieldGroup.setItemDataSource(medicalRecordDTO);

		Set<PhysicianDTO> physicians = medicalFacade.getAllPhysicians();
		final ComboBox<PhysicianDTO> physicianComboBox = new ComboBox<>("Ošetřující lékař", physicians);
		winLayout.addComponent(physicianComboBox, 0, 0, 1, 0);
		physicianComboBox.setWidth("100%");
		physicianComboBox.setEmptySelectionAllowed(false);
		// physicianComboBox.setImmediate(true);
		// fieldGroup.bind(physicianComboBox, "physician");

		final DateTimeField dateField = new DateTimeField("Datum návštěvy");
		dateField.setDateFormat("d. MMMMM yyyy, HH:mm");
		dateField.setLocale(Locale.forLanguageTag("CS"));
		dateField.setResolution(DateTimeResolution.MINUTE);
		winLayout.addComponent(dateField, 0, 1, 1, 1);
		dateField.setWidth("100%");
		// dateField.setImmediate(true);
		// fieldGroup.bind(dateField, "date");

		List<MedicalInstitutionDTO> institutions = medicalFacade.getAllMedicalInstitutions();
		final ComboBox<MedicalInstitutionDTO> institutionComboBox = new ComboBox<>("Instituce",
				medicalFacade.getAllMedicalInstitutions());
		winLayout.addComponent(institutionComboBox, 0, 2, 1, 2);
		institutionComboBox.setWidth("100%");
		institutionComboBox.setEmptySelectionAllowed(false);
		// institutionComboBox.setImmediate(true);
		// fieldGroup.bind(institutionComboBox, "institution");

		final TextArea recordField = new TextArea("Záznam");
		winLayout.addComponent(recordField, 0, 3, 1, 3);
		recordField.setWidth("100%");
		// recordField.setImmediate(true);
		// fieldGroup.bind(recordField, "record");

		final TwinColSelect<MedicamentDTO> typeSelect = new TwinColSelect<>("Medikamenty",
				medicalFacade.getAllMedicaments());
		typeSelect.setWidth("100%");
		typeSelect.setRows(7);
		// typeSelect.sete(true);
		// typeSelect.setMultiSelect(true);
		// typeSelect.setImmediate(true);
		// typeSelect.setItemCaptionPropertyId("name");
		// fieldGroup.bind(typeSelect, "medicaments");
		winLayout.addComponent(typeSelect, 0, 4, 1, 4);

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 5);

		Button saveBtn;
		winLayout.addComponent(
				saveBtn = new Button(recordDTO == null ? "Založit" : "Upravit", new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {
						try {
							fieldGroup.commit();
							if (medicalFacade.saveMedicalRecord(medicalRecordDTO) == false) {
								UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se uložit nový záznam"));
							} else {
								onSuccess();
							}
							close();
						} catch (CommitException e) {
							Notification.show("   Chybná vstupní data\n\n   " + e.getCause().getMessage(),
									Notification.Type.TRAY_NOTIFICATION);
						}
					}
				}), 1, 6);
		winLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);

		setContent(winLayout);

		addCloseListener(new CloseListener() {

			private static final long serialVersionUID = 1435044338717794371L;

			@Override
			public void windowClose(CloseEvent e) {
				triggerComponent.setEnabled(true);
			}

		});

	}

	protected abstract void onSuccess();

}
