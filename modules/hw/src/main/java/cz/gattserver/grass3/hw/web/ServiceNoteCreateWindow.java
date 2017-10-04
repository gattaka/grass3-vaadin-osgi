package cz.gattserver.grass3.hw.web;

import java.util.Arrays;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

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

	private ServiceNoteDTO serviceNoteDTO;

	public ServiceNoteCreateWindow(final Component triggerComponent, final HWItemDTO hwItem) {
		this(triggerComponent, hwItem, null);
	}

	public ServiceNoteCreateWindow(final Component triggerComponent, final HWItemDTO hwItem,
			final ServiceNoteDTO fixNote) {
		super(fixNote == null ? "Nový servisní záznam" : "Oprava existujícího servisního záznamu");

		setWidth("320px");

		triggerComponent.setEnabled(false);

		if (fixNote != null) {
			serviceNoteDTO = fixNote;
		} else {
			serviceNoteDTO = new ServiceNoteDTO();
			serviceNoteDTO.setDescription("");
			serviceNoteDTO.setState(hwItem.getState());
		}

		final BeanFieldGroup<ServiceNoteDTO> fieldGroup = new BeanFieldGroup<ServiceNoteDTO>(ServiceNoteDTO.class);
		fieldGroup.setItemDataSource(serviceNoteDTO);

		GridLayout winLayout = new GridLayout(2, 4);
		setContent(winLayout);
		winLayout.setSpacing(true);
		winLayout.setMargin(true);
		winLayout.setWidth("100%");

		DateField eventDateField = new DateField("Datum");
		eventDateField.setDateFormat("dd.MM.yyyy");
		eventDateField.setLocale(Locale.forLanguageTag("CS"));
		// fieldGroup.bind(eventDateField, "date");
		winLayout.addComponent(eventDateField, 0, 0);

		ComboBox stateComboBox = new ComboBox("Stav");
		stateComboBox.setNullSelectionAllowed(false);
		stateComboBox.setImmediate(true);
		stateComboBox.setContainerDataSource(
				new BeanItemContainer<HWItemState>(HWItemState.class, Arrays.asList(HWItemState.values())));
		stateComboBox.setItemCaptionPropertyId("name");
		fieldGroup.bind(stateComboBox, "state");
		winLayout.addComponent(stateComboBox, 1, 0);

		ComboBox usedInCombo = new ComboBox("Je součástí");
		fieldGroup.bind(usedInCombo, "usedInName");
		usedInCombo.setSizeFull();
		usedInCombo.setNullSelectionAllowed(true);
		usedInCombo.setImmediate(true);
		usedInCombo.setContainerDataSource(new BeanItemContainer<HWItemOverviewDTO>(HWItemOverviewDTO.class,
				hwFacade.getHWItemsAvailableForPart(hwItem)));
		usedInCombo.setItemCaptionPropertyId("name");
		usedInCombo.setValue(hwItem.getUsedIn());
		winLayout.addComponent(usedInCombo, 0, 1, 1, 1);

		TextArea descriptionField = new TextArea("Popis");
		descriptionField.setImmediate(true);
		fieldGroup.bind(descriptionField, "description");
		descriptionField.setWidth("100%");
		descriptionField.setHeight("120px");
		winLayout.addComponent(descriptionField, 0, 2, 1, 2);

		Button createBtn;
		winLayout.addComponent(
				createBtn = new Button(fixNote == null ? "Zapsat" : "Upravit", new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {

						try {
							fieldGroup.commit();
							if (fixNote == null) {
								if (hwFacade.addServiceNote(serviceNoteDTO, hwItem)) {
									onSuccess(serviceNoteDTO);
								} else {
									UI.getCurrent()
											.addWindow(new ErrorWindow("Nezdařilo se zapsat nový servisní záznam"));
								}
							} else {
								hwFacade.modifyServiceNote(serviceNoteDTO);
								onSuccess(serviceNoteDTO);
							}
							close();
						} catch (FieldGroup.CommitException e) {
							Notification.show("   Chybná vstupní data\n\n   " + e.getCause().getMessage(),
									Notification.Type.TRAY_NOTIFICATION);
						}

					}

				}), 1, 3);
		winLayout.setComponentAlignment(createBtn, Alignment.BOTTOM_RIGHT);

		addCloseListener(e -> triggerComponent.setEnabled(true));

	}

	protected abstract void onSuccess(ServiceNoteDTO noteDTO);

}
