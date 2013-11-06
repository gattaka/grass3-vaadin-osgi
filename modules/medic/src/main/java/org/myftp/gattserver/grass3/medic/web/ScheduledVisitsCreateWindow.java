package org.myftp.gattserver.grass3.medic.web;

import java.util.List;
import java.util.Locale;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicalRecordDTO;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitState;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;

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

public abstract class ScheduledVisitsCreateWindow extends GrassSubWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IMedicFacade medicalFacade;

	public ScheduledVisitsCreateWindow(final Component triggerComponent,
			boolean planned) {
		this(triggerComponent, planned, null);
	}

	public ScheduledVisitsCreateWindow(final Component triggerComponent,
			boolean planned, ScheduledVisitDTO visitDTO) {
		super(planned ? "Založení nové plánované návštěvy"
				: "Naplánování objednání");

		medicalFacade = SpringContextHelper.getBean(IMedicFacade.class);

		triggerComponent.setEnabled(false);

		GridLayout winLayout = new GridLayout(2, 6);
		winLayout.setWidth("350px");
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		final ScheduledVisitDTO scheduledVisitDTO = new ScheduledVisitDTO();
		scheduledVisitDTO.setPurpose("");
		scheduledVisitDTO.setPlanned(planned);
		scheduledVisitDTO.setState(planned ? ScheduledVisitState.PLANNED
				: ScheduledVisitState.TO_BE_PLANNED);
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
		winLayout.addComponent(saveBtn = new Button("Založit",
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
