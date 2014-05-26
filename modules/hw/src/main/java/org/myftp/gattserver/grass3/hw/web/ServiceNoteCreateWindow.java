package org.myftp.gattserver.grass3.hw.web;

import java.util.Arrays;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.dto.HWItemState;
import org.myftp.gattserver.grass3.hw.dto.ServiceNoteDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;
import org.myftp.gattserver.grass3.subwindows.ErrorWindow;
import org.myftp.gattserver.grass3.subwindows.GrassWindow;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;

public abstract class ServiceNoteCreateWindow extends GrassWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IHWFacade hwFacade;

	public ServiceNoteCreateWindow(final Component triggerComponent,
			final HWItemDTO hwItem) {
		super("Nový servisní záznam");

		hwFacade = SpringContextHelper.getBean(IHWFacade.class);

		setWidth("320px");

		triggerComponent.setEnabled(false);

		final ServiceNoteDTO serviceNoteDTO = new ServiceNoteDTO();
		serviceNoteDTO.setDescription("");
		serviceNoteDTO.setState(hwItem.getState());
		final BeanFieldGroup<ServiceNoteDTO> fieldGroup = new BeanFieldGroup<ServiceNoteDTO>(
				ServiceNoteDTO.class);
		fieldGroup.setItemDataSource(serviceNoteDTO);

		GridLayout winLayout = new GridLayout(2, 4);
		setContent(winLayout);
		winLayout.setSpacing(true);
		winLayout.setMargin(true);
		winLayout.setWidth("100%");

		DateField eventDateField = new DateField("Datum");
		fieldGroup.bind(eventDateField, "date");
		winLayout.addComponent(eventDateField, 0, 0);

		ComboBox stateComboBox = new ComboBox("Stav");
		stateComboBox.setNullSelectionAllowed(false);
		stateComboBox.setImmediate(true);
		stateComboBox
				.setContainerDataSource(new BeanItemContainer<HWItemState>(
						HWItemState.class, Arrays.asList(HWItemState.values())));
		stateComboBox.setItemCaptionPropertyId("name");
		fieldGroup.bind(stateComboBox, "state");
		winLayout.addComponent(stateComboBox, 1, 0);

		ComboBox usedInCombo = new ComboBox("Je součástí");
		fieldGroup.bind(usedInCombo, "usedIn");
		usedInCombo.setSizeFull();
		usedInCombo.setNullSelectionAllowed(true);
		usedInCombo.setImmediate(true);
		usedInCombo.setContainerDataSource(new BeanItemContainer<HWItemDTO>(
				HWItemDTO.class, hwFacade.getHWItemsAvailableForPart(hwItem)));
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
		winLayout.addComponent(createBtn = new Button("Zapsat",
				new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {

						try {
							fieldGroup.commit();
							if (hwFacade.addServiceNote(serviceNoteDTO, hwItem)) {
								onSuccess();
							} else {
								UI.getCurrent()
										.addWindow(
												new ErrorWindow(
														"Nezdařilo se zapsat nový servisní záznam"));
							}
							close();
						} catch (FieldGroup.CommitException e) {
							Notification.show("   Chybná vstupní data\n\n   "
									+ e.getCause().getMessage(),
									Notification.Type.TRAY_NOTIFICATION);
						}

					}

				}), 1, 3);
		winLayout.setComponentAlignment(createBtn, Alignment.BOTTOM_RIGHT);

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
