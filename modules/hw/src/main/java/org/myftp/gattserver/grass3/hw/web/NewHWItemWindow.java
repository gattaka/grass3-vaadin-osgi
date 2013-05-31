package org.myftp.gattserver.grass3.hw.web;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.dto.HWItemState;
import org.myftp.gattserver.grass3.hw.dto.HWItemTypeDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public abstract class NewHWItemWindow extends Window {

	private static final long serialVersionUID = -6773027334692911384L;

	private IHWFacade hwFacade;

	public NewHWItemWindow(final Button newType) {
		super("Založení nové položky HW");

		hwFacade = SpringContextHelper.getBean(IHWFacade.class);

		final HWItemDTO hwItemDTO = new HWItemDTO();
		hwItemDTO.setName("");
		hwItemDTO.setPrice(0);
		hwItemDTO.setUsage("");
		final BeanFieldGroup<HWItemDTO> fieldGroup = new BeanFieldGroup<HWItemDTO>(
				HWItemDTO.class);
		fieldGroup.setItemDataSource(hwItemDTO);

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		GridLayout winLayout = new GridLayout(2, 4);
		layout.addComponent(winLayout);
		winLayout.setWidth("400px");
		winLayout.setSpacing(true);

		TextField nameField = new TextField("Název");
		nameField.setImmediate(true);
		nameField.setWidth("100%");
		fieldGroup.bind(nameField, "name");
		winLayout.addComponent(nameField, 0, 0);

		DateField purchaseDateField = new DateField("Získáno");
		fieldGroup.bind(purchaseDateField, "purchaseDate");
		purchaseDateField.setSizeFull();
		winLayout.addComponent(purchaseDateField, 0, 1);

		DateField destructionDateField = new DateField("Odepsáno");
		fieldGroup.bind(destructionDateField, "destructionDate");
		destructionDateField.setSizeFull();
		winLayout.addComponent(destructionDateField, 0, 2);

		TextField priceField = new TextField("Cena");
		fieldGroup.bind(priceField, "price");
		priceField.setSizeFull();
		winLayout.addComponent(priceField, 1, 0);

		ComboBox stateComboBox = new ComboBox("Stav");
		stateComboBox.setWidth("100%");
		stateComboBox.setNullSelectionAllowed(false);
		stateComboBox.setImmediate(true);
		stateComboBox
				.setContainerDataSource(new BeanItemContainer<HWItemState>(
						HWItemState.class, Arrays.asList(HWItemState.values())));
		stateComboBox.setItemCaptionPropertyId("name");
		fieldGroup.bind(stateComboBox, "state");
		winLayout.addComponent(stateComboBox, 1, 1);

		TextField usageField = new TextField("Je součástí");
		fieldGroup.bind(usageField, "usage");
		usageField.setSizeFull();
		winLayout.addComponent(usageField, 1, 2);

		Set<HWItemTypeDTO> types = hwFacade.getAllHWTypes();

		BeanItemContainer<HWItemTypeDTO> typeSelectContainer = new BeanItemContainer<HWItemTypeDTO>(
				HWItemTypeDTO.class, types);
		final TwinColSelect typeSelect = new TwinColSelect("Typy",
				typeSelectContainer);
		typeSelect.setWidth("100%");
		typeSelect.setRows(7);
		typeSelect.setNullSelectionAllowed(true);
		typeSelect.setMultiSelect(true);
		typeSelect.setImmediate(true);
		typeSelect.setItemCaptionPropertyId("name");
		fieldGroup.bind(typeSelect, "types");
		winLayout.addComponent(typeSelect, 0, 3, 1, 3);

		Button createBtn;
		layout.addComponent(createBtn = new Button("Založit",
				new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {

						try {
							fieldGroup.commit();
							if (hwFacade.saveHWItem(hwItemDTO)) {
								onSuccess();
							} else {
								UI.getCurrent()
										.addWindow(
												new Window(
														"Chyba",
														new Label(
																"Nezdařilo se vytvořit novou položku hardware")));
							}
							close();
						} catch (FieldGroup.CommitException e) {
							Notification.show("   Chybná vstupní data\n\n   "
									+ e.getCause().getMessage(),
									Notification.Type.TRAY_NOTIFICATION);
						}

					}

				}));
		layout.setComponentAlignment(createBtn, Alignment.BOTTOM_RIGHT);

		setContent(layout);

		addCloseListener(new CloseListener() {

			private static final long serialVersionUID = 1435044338717794371L;

			@Override
			public void windowClose(CloseEvent e) {
				newType.setEnabled(true);
			}

		});

	}

	protected abstract void onSuccess();

}
