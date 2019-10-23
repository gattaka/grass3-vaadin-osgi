package cz.gattserver.grass3.hw.ui.dialogs;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass3.hw.interfaces.HWItemState;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.ui.components.SaveCloseButtons;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class ServiceNoteCreateDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient HWService hwService;

	public ServiceNoteCreateDialog(final HWItemTO hwItem) {
		this(hwItem, null);
	}

	public ServiceNoteCreateDialog(final HWItemTO hwItem, ServiceNoteTO originalTO) {
		ServiceNoteTO formTO = new ServiceNoteTO();
		formTO.setDate(LocalDate.now());
		formTO.setDescription("");
		formTO.setState(hwItem.getState());

		Binder<ServiceNoteTO> binder = new Binder<>();
		binder.setBean(formTO);

		FormLayout winLayout = new FormLayout();
		add(winLayout);

		DatePicker eventDateField = new DatePicker("Datum");
		eventDateField.setLocale(Locale.forLanguageTag("CS"));
		binder.forField(eventDateField).asRequired("Datum musí být vyplněno").bind(ServiceNoteTO::getDate,
				ServiceNoteTO::setDate);
		winLayout.add(eventDateField);

		ComboBox<HWItemState> stateComboBox = new ComboBox<>("Stav", Arrays.asList(HWItemState.values()));
		// namísto propertyId a captionId jsou funkcionální settery a gettery
		stateComboBox.setItemLabelGenerator(HWItemState::getName);
		binder.forField(stateComboBox).bind(ServiceNoteTO::getState, ServiceNoteTO::setState);
		winLayout.add(stateComboBox);

		ComboBox<HWItemOverviewTO> usedInCombo = new ComboBox<>("Je součástí",
				getHWService().getHWItemsAvailableForPart(hwItem.getId()));
		usedInCombo.setSizeFull();
		usedInCombo.setItemLabelGenerator(HWItemOverviewTO::getName);
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
		winLayout.add(usedInCombo);

		if (hwItem.getUsedIn() != null)
			usedInCombo.setValue(hwItem.getUsedIn());

		TextArea descriptionField = new TextArea("Popis");
		descriptionField.setWidth("100%");
		descriptionField.setHeight("120px");
		binder.forField(descriptionField).bind(ServiceNoteTO::getDescription, ServiceNoteTO::setDescription);
		winLayout.add(descriptionField);

		SaveCloseButtons buttons = new SaveCloseButtons(e -> {
			try {
				ServiceNoteTO writeDTO = originalTO == null ? new ServiceNoteTO() : originalTO;
				binder.writeBean(writeDTO);
				if (originalTO == null) {
					getHWService().addServiceNote(writeDTO, hwItem.getId());
					onSuccess(writeDTO);
				} else {
					getHWService().modifyServiceNote(writeDTO);
					onSuccess(writeDTO);
				}
				close();
			} catch (Exception ex) {
				new ErrorDialog("Nezdařilo se zapsat nový servisní záznam").open();
			}
		}, e -> close());

		add(buttons);

		// Poté, co je form probindován se nastaví hodnoty dle originálu
		if (originalTO != null)
			binder.readBean(originalTO);
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	protected abstract void onSuccess(ServiceNoteTO noteDTO);

}
