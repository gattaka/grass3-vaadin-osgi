package cz.gattserver.grass3.medic.web;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import cz.gattserver.grass3.medic.dto.MedicalRecordDTO;
import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.dto.ScheduledVisitState;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class ScheduledVisitsCreateWindow extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private static final Logger logger = LoggerFactory.getLogger(ScheduledVisitsCreateWindow.class);

	private static final String PLANNED_CREATION_TITLE = "Založení nové plánované návštěvy";
	private static final String TO_BE_PLANNED_CREATION_TITLE = "Naplánování objednání";
	private static final String PLANNED_EDIT_TITLE = "Úprava plánované návštěvy";
	private static final String TO_BE_PLANNED_EDIT_TITLE = "Úprava naplánování objednání";
	private static final String CREATE_BTN_CAPTION = "Založit";
	private static final String EDIT_BTN_CAPTION = "Upravit";

	enum Operation {
		TO_BE_PLANNED, PLANNED, PLANNED_FROM_TO_BE_PLANNED
	}

	public ScheduledVisitsCreateWindow(Operation operation) {
		this(operation, null);
	}

	public ScheduledVisitsCreateWindow(Operation operation, ScheduledVisitDTO originalDTO) {
		super(getTitleByOperation(operation, originalDTO));

		boolean planned = operation.equals(Operation.PLANNED) || operation.equals(Operation.PLANNED_FROM_TO_BE_PLANNED);

		MedicFacade medicalFacade = SpringContextHelper.getBean(MedicFacade.class);

		GridLayout winLayout = new GridLayout(2, 6);
		winLayout.setWidth("350px");
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		ScheduledVisitDTO formDTO = new ScheduledVisitDTO();
		formDTO.setPurpose("");
		formDTO.setPlanned(planned);
		formDTO.setState(planned ? ScheduledVisitState.PLANNED : ScheduledVisitState.TO_BE_PLANNED);

		Binder<ScheduledVisitDTO> binder = new Binder<>(ScheduledVisitDTO.class);
		binder.setBean(formDTO);

		final TextField purposeField = new TextField("Účel návštěvy");
		winLayout.addComponent(purposeField, 0, 0, 1, 0);
		purposeField.setWidth("100%");
		binder.forField(purposeField).asRequired().bind("purpose");

		if (!planned) {
			final TextField periodField = new TextField("Pravidelnost (měsíce)");
			winLayout.addComponent(periodField, 0, 1);
			periodField.setWidth("100%");
			binder.forField(periodField)
					.withConverter(new StringToIntegerConverter(0, "Počet měsíců musí být celé číslo")).bind("period");
		}

		final DateTimeField dateField = new DateTimeField("Datum návštěvy");
		dateField.setLocale(Locale.forLanguageTag("CS"));
		if (planned) {
			dateField.setResolution(DateTimeResolution.MINUTE);
			dateField.setDateFormat("d. MMMM yyyy, HH:mm");
			winLayout.addComponent(dateField, 0, 1, 1, 1);
		} else {
			dateField.setResolution(DateTimeResolution.MONTH);
			dateField.setDateFormat("MMMMM yyyy");
			winLayout.addComponent(dateField, 1, 1);
		}

		dateField.setWidth("100%");
		binder.forField(dateField).asRequired().bind("date");

		List<MedicalRecordDTO> records = medicalFacade.getAllMedicalRecords();
		final ComboBox<MedicalRecordDTO> recordsComboBox = new ComboBox<>("Navazuje na kontrolu", records);
		winLayout.addComponent(recordsComboBox, 0, 2, 1, 2);
		recordsComboBox.setWidth("100%");
		binder.forField(recordsComboBox).bind("record");

		List<MedicalInstitutionDTO> institutions = medicalFacade.getAllMedicalInstitutions();
		final ComboBox<MedicalInstitutionDTO> institutionComboBox = new ComboBox<>("Instituce", institutions);
		winLayout.addComponent(institutionComboBox, 0, 3, 1, 3);
		institutionComboBox.setWidth("100%");
		institutionComboBox.setEmptySelectionAllowed(false);
		binder.forField(institutionComboBox).asRequired().bind("institution");

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 4);

		Button saveBtn = new Button(getSubmitBtnCaptionByOperation(operation, originalDTO), e -> {
			try {
				ScheduledVisitDTO writeDTO = originalDTO == null ? formDTO : originalDTO;
				binder.writeBean(writeDTO);
				medicalFacade.saveScheduledVisit(writeDTO);
				onSuccess();
				close();
			} catch (ValidationException ex) {
				Notification.show(
						"Chybná vstupní data\n\n   " + ex.getValidationErrors().iterator().next().getErrorMessage(),
						Notification.Type.ERROR_MESSAGE);
			} catch (Exception ex) {
				String msg = "Nezdařilo se vytvořit nový záznam";
				UI.getCurrent().addWindow(new ErrorDialog(msg));
				logger.error(msg, ex);
			}
		});
		winLayout.addComponent(saveBtn, 1, 5);
		winLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);

		if (originalDTO != null)
			binder.readBean(originalDTO);

		// vyplňuji objednání na základě plánovaného objednání
		if (originalDTO != null && planned) {
			purposeField.setValue(originalDTO.getPurpose());
			recordsComboBox.setValue(originalDTO.getRecord());
			institutionComboBox.setValue(originalDTO.getInstitution());
		}

		setContent(winLayout);
	}

	private static String getTitleByOperation(Operation operation, ScheduledVisitDTO visitDTO) {
		switch (operation) {
		case PLANNED:
			return visitDTO == null ? PLANNED_CREATION_TITLE : PLANNED_EDIT_TITLE;
		case PLANNED_FROM_TO_BE_PLANNED:
			return PLANNED_CREATION_TITLE;
		case TO_BE_PLANNED:
			return visitDTO == null ? TO_BE_PLANNED_CREATION_TITLE : TO_BE_PLANNED_EDIT_TITLE;
		default:
			// assert !
			return null;
		}
	}

	private static String getSubmitBtnCaptionByOperation(Operation operation, ScheduledVisitDTO visitDTO) {
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

	protected abstract void onSuccess();

}
