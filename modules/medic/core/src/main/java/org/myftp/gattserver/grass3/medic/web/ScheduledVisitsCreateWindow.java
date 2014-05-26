package org.myftp.gattserver.grass3.medic.web;

import java.util.List;
import java.util.Locale;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicalRecordDTO;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitState;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.subwindows.ErrorWindow;
import org.myftp.gattserver.grass3.subwindows.GrassWindow;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public abstract class ScheduledVisitsCreateWindow extends GrassWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IMedicFacade medicalFacade;

	private static final String PLANNED_CREATION_TITLE = "Založení nové plánované návštěvy";
	private static final String TO_BE_PLANNED_CREATION_TITLE = "Naplánování objednání";
	private static final String PLANNED_EDIT_TITLE = "Úprava plánované návštěvy";
	private static final String TO_BE_PLANNED_EDIT_TITLE = "Úprava naplánování objednání";
	private static final String CREATE_BTN_CAPTION = "Založit";
	private static final String EDIT_BTN_CAPTION = "Upravit";

	enum Operation {
		TO_BE_PLANNED, PLANNED, PLANNED_FROM_TO_BE_PLANNED
	}

	public ScheduledVisitsCreateWindow(final Component triggerComponent,
			Operation operation) {
		this(triggerComponent, operation, null);
	}

	private static String getTitleByOperation(Operation operation,
			ScheduledVisitDTO visitDTO) {
		switch (operation) {
		case PLANNED:
			return visitDTO == null ? PLANNED_CREATION_TITLE
					: PLANNED_EDIT_TITLE;
		case PLANNED_FROM_TO_BE_PLANNED:
			return PLANNED_CREATION_TITLE;
		case TO_BE_PLANNED:
			return visitDTO == null ? TO_BE_PLANNED_CREATION_TITLE
					: TO_BE_PLANNED_EDIT_TITLE;
		default:
			// assert !
			return null;
		}
	}

	private static String getSubmitBtnCaptionByOperation(Operation operation,
			ScheduledVisitDTO visitDTO) {
		switch (operation) {
		case PLANNED:
		case TO_BE_PLANNED:
			return visitDTO == null ? CREATE_BTN_CAPTION : EDIT_BTN_CAPTION;
		case PLANNED_FROM_TO_BE_PLANNED:
			return CREATE_BTN_CAPTION;
		default:
			// assert !
			return null;
		}
	}

	public ScheduledVisitsCreateWindow(final Component triggerComponent,
			Operation operation, ScheduledVisitDTO visitDTO) {
		super(getTitleByOperation(operation, visitDTO));

		boolean planned = operation.equals(Operation.PLANNED)
				|| operation.equals(Operation.PLANNED_FROM_TO_BE_PLANNED);

		medicalFacade = SpringContextHelper.getBean(IMedicFacade.class);

		triggerComponent.setEnabled(false);

		GridLayout winLayout = new GridLayout(2, 6);
		winLayout.setWidth("350px");
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		final ScheduledVisitDTO scheduledVisitDTO = visitDTO == null ? new ScheduledVisitDTO()
				: visitDTO;
		if (visitDTO == null) {
			scheduledVisitDTO.setPurpose("");
			scheduledVisitDTO.setPlanned(planned);
			scheduledVisitDTO.setState(planned ? ScheduledVisitState.PLANNED
					: ScheduledVisitState.TO_BE_PLANNED);
		}

		final BeanFieldGroup<ScheduledVisitDTO> fieldGroup = new BeanFieldGroup<ScheduledVisitDTO>(
				ScheduledVisitDTO.class);
		fieldGroup.setItemDataSource(scheduledVisitDTO);

		final TextField purposeField = new TextField("Účel návštěvy");
		winLayout.addComponent(purposeField, 0, 0, 1, 0);
		purposeField.setWidth("100%");
		purposeField.setImmediate(true);
		fieldGroup.bind(purposeField, "purpose");

		if (planned == false) {
			final TextField periodField = new TextField("Pravidelnost (měsíce)");
			winLayout.addComponent(periodField, 0, 1);
			periodField.setWidth("100%");
			fieldGroup.bind(periodField, "period");
		}

		final DateField dateField = new DateField("Datum návštěvy");
		dateField.setLocale(Locale.forLanguageTag("CS"));
		if (planned) {
			dateField.setResolution(Resolution.MINUTE);
			dateField.setDateFormat("d. MMMMM yyyy, HH:mm");
			winLayout.addComponent(dateField, 0, 1, 1, 1);
		} else {
			dateField.setResolution(Resolution.MONTH);
			dateField.setDateFormat("MMMMM yyyy");
			winLayout.addComponent(dateField, 1, 1);
		}

		dateField.setWidth("100%");
		dateField.setImmediate(true);
		fieldGroup.bind(dateField, "date");

		List<MedicalRecordDTO> records = medicalFacade.getAllMedicalRecords();
		final ComboBox recordsComboBox = new ComboBox("Navazuje na kontrolu",
				records);
		winLayout.addComponent(recordsComboBox, 0, 2, 1, 2);
		recordsComboBox.setWidth("100%");
		fieldGroup.bind(recordsComboBox, "record");

		List<MedicalInstitutionDTO> institutions = medicalFacade
				.getAllMedicalInstitutions();
		final ComboBox institutionComboBox = new ComboBox("Instituce",
				institutions);
		winLayout.addComponent(institutionComboBox, 0, 3, 1, 3);
		institutionComboBox.setWidth("100%");
		institutionComboBox.setNullSelectionAllowed(false);
		institutionComboBox.setImmediate(true);
		fieldGroup.bind(institutionComboBox, "institution");

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 4);

		Button saveBtn;
		winLayout.addComponent(saveBtn = new Button(
				getSubmitBtnCaptionByOperation(operation, visitDTO),
				new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {
						try {
							fieldGroup.commit();
							if (medicalFacade
									.saveScheduledVisit(scheduledVisitDTO) == false) {
								UI.getCurrent()
										.addWindow(
												new ErrorWindow(
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
				}), 1, 5);
		winLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);

		// vyplňuji objednání na základě plánovaného objednání
		if (visitDTO != null && planned) {
			purposeField.setValue(visitDTO.getPurpose());
			recordsComboBox.setValue(visitDTO.getRecord());
			institutionComboBox.setValue(visitDTO.getInstitution());
		}

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
