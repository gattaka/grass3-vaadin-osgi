package cz.gattserver.grass3.hw.web;

import java.util.Arrays;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.hw.dto.HWItemDTO;
import cz.gattserver.grass3.hw.dto.HWItemOverviewDTO;
import cz.gattserver.grass3.hw.dto.HWItemState;
import cz.gattserver.grass3.hw.dto.ServiceNoteDTO;
import cz.gattserver.grass3.hw.facade.HWFacade;
import cz.gattserver.web.common.window.ErrorWindow;
import cz.gattserver.web.common.window.WebWindow;

public abstract class ServiceNoteCreateWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	@Autowired
	private HWFacade hwFacade;

	public ServiceNoteCreateWindow(final Component triggerComponent, final HWItemDTO hwItem) {
		this(triggerComponent, hwItem, null);
	}

	public ServiceNoteCreateWindow(Component triggerComponent, final HWItemDTO hwItem, ServiceNoteDTO originalDTO) {
		super(originalDTO == null ? "Nový servisní záznam" : "Oprava existujícího servisního záznamu");

		triggerComponent.setEnabled(false);

		ServiceNoteDTO formDTO = new ServiceNoteDTO();
		formDTO.setDescription("");
		formDTO.setState(hwItem.getState());

		Binder<ServiceNoteDTO> binder = new Binder<>(ServiceNoteDTO.class);
		binder.setBean(formDTO);

		GridLayout winLayout = new GridLayout(2, 4);
		setContent(winLayout);
		winLayout.setSpacing(true);
		winLayout.setMargin(true);

		DateField eventDateField = new DateField("Datum");
		eventDateField.setDateFormat("dd.MM.yyyy");
		eventDateField.setLocale(Locale.forLanguageTag("CS"));
		binder.bind(eventDateField, "date");
		winLayout.addComponent(eventDateField, 0, 0);

		ComboBox<HWItemState> stateComboBox = new ComboBox<>("Stav", Arrays.asList(HWItemState.values()));
		stateComboBox.setEmptySelectionAllowed(false);
		// namísto propertyId a captionId jsou funkcionální settery a gettery
		stateComboBox.setItemCaptionGenerator(a -> a.getName());
		binder.bind(stateComboBox, "state");
		winLayout.addComponent(stateComboBox, 1, 0);

		ComboBox<HWItemOverviewDTO> usedInCombo = new ComboBox<>("Je součástí",
				hwFacade.getHWItemsAvailableForPart(hwItem));
		usedInCombo.setSizeFull();
		usedInCombo.setEmptySelectionAllowed(true);
		usedInCombo.setItemCaptionGenerator(a -> a.getName());
		usedInCombo.setValue(hwItem.getUsedIn());
		// ekvivalent Convertoru z v7
		binder.bind(usedInCombo, note -> {
			if (note.getUsedInName() == null)
				return null;
			HWItemOverviewDTO to = new HWItemOverviewDTO();
			to.setId(note.getUsedInId());
			to.setName(note.getUsedInName());
			return to;
		}, (note, item) -> {
			note.setUsedInId(item == null ? null : item.getId());
			note.setUsedInName(item == null ? null : item.getName());
		});
		winLayout.addComponent(usedInCombo, 0, 1, 1, 1);

		TextArea descriptionField = new TextArea("Popis");
		descriptionField.setWidth("100%");
		descriptionField.setHeight("120px");
		binder.bind(descriptionField, "description");
		winLayout.addComponent(descriptionField, 0, 2, 1, 2);

		Button createBtn;
		winLayout.addComponent(createBtn = new Button(originalDTO == null ? "Zapsat" : "Upravit", e -> {
			try {
				ServiceNoteDTO writeDTO = originalDTO == null ? new ServiceNoteDTO() : originalDTO;
				binder.writeBean(writeDTO);
				if (originalDTO == null) {
					hwFacade.addServiceNote(writeDTO, hwItem);
					onSuccess(writeDTO);
				} else {
					hwFacade.modifyServiceNote(writeDTO);
					onSuccess(writeDTO);
				}
				close();
			} catch (ValidationException ex) {
				Notification.show("   Chybná vstupní data\n\n   " + ex.getBeanValidationErrors().iterator().next(),
						Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception ex) {
				UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se zapsat nový servisní záznam"));
			}
		}), 1, 3);
		winLayout.setComponentAlignment(createBtn, Alignment.BOTTOM_RIGHT);

		addCloseListener(e -> triggerComponent.setEnabled(true));

		// Poté, co je form probindován se nastaví hodnoty dle originálu
		if (originalDTO != null)
			binder.readBean(originalDTO);
	}

	protected abstract void onSuccess(ServiceNoteDTO noteDTO);

}
