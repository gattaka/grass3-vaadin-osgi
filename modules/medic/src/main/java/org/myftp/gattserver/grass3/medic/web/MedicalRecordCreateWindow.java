package org.myftp.gattserver.grass3.medic.web;

import java.util.List;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicalRecordDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicamentDTO;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;

public abstract class MedicalRecordCreateWindow extends GrassSubWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IMedicFacade medicalFacade;

	public MedicalRecordCreateWindow(final Component triggerComponent) {
		this(triggerComponent, null);
	}

	public MedicalRecordCreateWindow(final Component triggerComponent,
			ScheduledVisitDTO scheduledVisitDTO) {
		super("Založení nového záznamu");

		medicalFacade = SpringContextHelper.getBean(IMedicFacade.class);

		triggerComponent.setEnabled(false);

		GridLayout winLayout = new GridLayout(2, 7);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		winLayout.setWidth("400px");

		final MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO();
		medicalRecordDTO.setDoctor("");
		medicalRecordDTO.setRecord("");

		if (scheduledVisitDTO != null) {
			medicalRecordDTO.setDate(scheduledVisitDTO.getDate());
			medicalRecordDTO.setInstitution(scheduledVisitDTO.getInstitution());
		}

		final BeanFieldGroup<MedicalRecordDTO> fieldGroup = new BeanFieldGroup<MedicalRecordDTO>(
				MedicalRecordDTO.class);
		fieldGroup.setItemDataSource(medicalRecordDTO);

		final TextField nameField = new TextField("Ošetřující lékař");
		winLayout.addComponent(nameField, 0, 0, 1, 0);
		nameField.setWidth("100%");
		fieldGroup.bind(nameField, "doctor");

		final DateField dateField = new DateField("Datum návštěvy");
		dateField.setDateFormat("dd.MM.yyyy HH:mm");
		dateField.setResolution(Resolution.MINUTE);
		winLayout.addComponent(dateField, 0, 1, 1, 1);
		dateField.setWidth("100%");
		fieldGroup.bind(dateField, "date");

		List<MedicalInstitutionDTO> institutions = medicalFacade
				.getAllMedicalInstitutions();
		BeanItemContainer<MedicalInstitutionDTO> institutionsContainer = new BeanItemContainer<>(
				MedicalInstitutionDTO.class, institutions);
		final ComboBox institutionComboBox = new ComboBox("Instituce",
				institutionsContainer);
		winLayout.addComponent(institutionComboBox, 0, 2, 1, 2);
		institutionComboBox.setWidth("100%");
		institutionComboBox.setNullSelectionAllowed(false);
		fieldGroup.bind(institutionComboBox, "institution");

		final TextArea hoursField = new TextArea("Záznam");
		winLayout.addComponent(hoursField, 0, 3, 1, 3);
		hoursField.setWidth("100%");
		fieldGroup.bind(hoursField, "record");

		BeanItemContainer<MedicamentDTO> medicamentsContainer = new BeanItemContainer<MedicamentDTO>(
				MedicamentDTO.class, medicalFacade.getAllMedicaments());
		final TwinColSelect typeSelect = new TwinColSelect("Medikamenty",
				medicamentsContainer);
		typeSelect.setWidth("100%");
		typeSelect.setRows(7);
		typeSelect.setNullSelectionAllowed(true);
		typeSelect.setMultiSelect(true);
		typeSelect.setImmediate(true);
		typeSelect.setItemCaptionPropertyId("name");
		fieldGroup.bind(typeSelect, "medicaments");
		winLayout.addComponent(typeSelect, 0, 4, 1, 4);

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 5);

		Button saveBtn;
		winLayout.addComponent(saveBtn = new Button("Založit",
				new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {
						try {
							fieldGroup.commit();
							if (medicalFacade
									.saveMedicalRecord(medicalRecordDTO) == false) {
								UI.getCurrent()
										.addWindow(
												new ErrorSubwindow(
														"Nezdařilo se vytvořit nový záznam"));
							} else {
								onSuccess();
							}
							close();
						} catch (CommitException e) {
							Notification.show("   Chybná vstupní data\n\n   "
									+ e.getCause().getMessage(),
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
