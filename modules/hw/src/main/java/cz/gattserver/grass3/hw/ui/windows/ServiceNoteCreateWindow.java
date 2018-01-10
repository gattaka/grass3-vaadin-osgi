package cz.gattserver.grass3.hw.ui.windows;

import java.util.Arrays;
import java.util.Locale;

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

import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass3.hw.interfaces.HWItemState;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class ServiceNoteCreateWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient HWService hwService;

	public ServiceNoteCreateWindow(final Component triggerComponent, final HWItemTO hwItem) {
		this(triggerComponent, hwItem, null);
	}

	public ServiceNoteCreateWindow(Component triggerComponent, final HWItemTO hwItem, ServiceNoteTO originalDTO) {
		super(originalDTO == null ? "Nový servisní záznam" : "Oprava existujícího servisního záznamu");

		triggerComponent.setEnabled(false);

		ServiceNoteTO formDTO = new ServiceNoteTO();
		formDTO.setDescription("");
		formDTO.setState(hwItem.getState());

		Binder<ServiceNoteTO> binder = new Binder<>(ServiceNoteTO.class);
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
		stateComboBox.setItemCaptionGenerator(HWItemState::getName);
		binder.bind(stateComboBox, "state");
		winLayout.addComponent(stateComboBox, 1, 0);

		ComboBox<HWItemOverviewTO> usedInCombo = new ComboBox<>("Je součástí",
				getHWService().getHWItemsAvailableForPart(hwItem));
		usedInCombo.setSizeFull();
		usedInCombo.setEmptySelectionAllowed(true);
		usedInCombo.setItemCaptionGenerator(HWItemOverviewTO::getName);
		usedInCombo.setValue(hwItem.getUsedIn());
		// ekvivalent Convertoru z v7
		binder.bind(usedInCombo, note -> {
			if (note.getUsedInName() == null)
				return null;
			HWItemOverviewTO to = new HWItemOverviewTO();
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
		createBtn = new Button(originalDTO == null ? "Zapsat" : "Upravit", e -> {
			try {
				ServiceNoteTO writeDTO = originalDTO == null ? new ServiceNoteTO() : originalDTO;
				binder.writeBean(writeDTO);
				if (originalDTO == null) {
					getHWService().addServiceNote(writeDTO, hwItem);
					onSuccess(writeDTO);
				} else {
					getHWService().modifyServiceNote(writeDTO);
					onSuccess(writeDTO);
				}
				close();
			} catch (ValidationException ex) {
				Notification.show("   Chybná vstupní data\n\n   " + ex.getBeanValidationErrors().iterator().next(),
						Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception ex) {
				UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se zapsat nový servisní záznam"));
			}
		});
		winLayout.addComponent(createBtn, 1, 3);
		winLayout.setComponentAlignment(createBtn, Alignment.BOTTOM_RIGHT);

		addCloseListener(e -> triggerComponent.setEnabled(true));

		// Poté, co je form probindován se nastaví hodnoty dle originálu
		if (originalDTO != null)
			binder.readBean(originalDTO);
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	protected abstract void onSuccess(ServiceNoteTO noteDTO);

}
