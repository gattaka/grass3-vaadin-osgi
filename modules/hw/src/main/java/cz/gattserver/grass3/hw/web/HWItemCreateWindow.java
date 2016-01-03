package cz.gattserver.grass3.hw.web;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

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
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.hw.dto.HWItemDTO;
import cz.gattserver.grass3.hw.dto.HWItemState;
import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.facade.IHWFacade;
import cz.gattserver.web.common.window.ErrorWindow;
import cz.gattserver.web.common.window.WebWindow;

public abstract class HWItemCreateWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IHWFacade hwFacade;

	/**
	 * @param triggerComponent
	 *            volající komponenta (ta, která má být po dobu zobrazení okna zablokována)
	 * @param fixItemId
	 *            opravuji údaje existující položky, nebo vytvářím novou ( {@code null}) ?
	 */
	public HWItemCreateWindow(final Component triggerComponent, final Long fixItemId) {
		super(fixItemId == null ? "Založení nové položky HW" : "Oprava údajů existující položky HW");

		hwFacade = SpringContextHelper.getBean(IHWFacade.class);

		triggerComponent.setEnabled(false);

		HWItemDTO hwItemDTO;
		if (fixItemId != null) {
			hwItemDTO = hwFacade.getHWItem(fixItemId);
		} else {
			hwItemDTO = new HWItemDTO();
			hwItemDTO.setName("");
			hwItemDTO.setPrice(0);
			hwItemDTO.setWarrantyYears(0);
		}

		final BeanFieldGroup<HWItemDTO> fieldGroup = new BeanFieldGroup<HWItemDTO>(HWItemDTO.class);
		fieldGroup.setItemDataSource(hwItemDTO);

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		GridLayout winLayout = new GridLayout(2, 5);
		layout.addComponent(winLayout);
		winLayout.setWidth("400px");
		winLayout.setSpacing(true);

		TextField nameField = new TextField("Název");
		nameField.setImmediate(true);
		nameField.setWidth("100%");
		fieldGroup.bind(nameField, "name");
		winLayout.addComponent(nameField, 0, 0);

		DateField purchaseDateField = new DateField("Získáno");
		purchaseDateField.setDateFormat("dd.MM.yyyy");
		purchaseDateField.setLocale(Locale.forLanguageTag("CS"));
		fieldGroup.bind(purchaseDateField, "purchaseDate");
		purchaseDateField.setSizeFull();
		winLayout.addComponent(purchaseDateField, 0, 1);

		DateField destructionDateField = new DateField("Odepsáno");
		destructionDateField.setDateFormat("dd.MM.yyyy");
		destructionDateField.setLocale(Locale.forLanguageTag("CS"));
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
		stateComboBox.setContainerDataSource(new BeanItemContainer<HWItemState>(HWItemState.class, Arrays
				.asList(HWItemState.values())));
		stateComboBox.setItemCaptionPropertyId("name");
		fieldGroup.bind(stateComboBox, "state");
		winLayout.addComponent(stateComboBox, 1, 1);

		TextField warrantyYearsField = new TextField("Záruka (roky)");
		fieldGroup.bind(warrantyYearsField, "warrantyYears");
		warrantyYearsField.setSizeFull();
		winLayout.addComponent(warrantyYearsField, 1, 2);

		TextField supervizedForField = new TextField("Spravováno pro");
		supervizedForField.setImmediate(true);
		supervizedForField.setWidth("100%");
		supervizedForField.setNullRepresentation("");
		fieldGroup.bind(supervizedForField, "supervizedFor");
		winLayout.addComponent(supervizedForField, 0, 3, 1, 3);

		Set<HWItemTypeDTO> types = hwFacade.getAllHWTypes();

		BeanItemContainer<HWItemTypeDTO> typeSelectContainer = new BeanItemContainer<HWItemTypeDTO>(
				HWItemTypeDTO.class, types);
		final TwinColSelect typeSelect = new TwinColSelect("Typy", typeSelectContainer);
		typeSelect.setWidth("100%");
		typeSelect.setRows(7);
		typeSelect.setNullSelectionAllowed(true);
		typeSelect.setMultiSelect(true);
		typeSelect.setImmediate(true);
		typeSelect.setItemCaptionPropertyId("name");
		fieldGroup.bind(typeSelect, "types");
		winLayout.addComponent(typeSelect, 0, 4, 1, 4);

		Button createBtn;
		layout.addComponent(createBtn = new Button(fixItemId == null ? "Založit" : "Opravit údaje",
				new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {

						try {
							fieldGroup.commit();
							if (hwFacade.saveHWItem(fieldGroup.getItemDataSource().getBean())) {
								onSuccess();
							} else {
								UI.getCurrent().addWindow(
										new ErrorWindow(
												fixItemId == null ? "Nezdařilo se vytvořit novou položku hardware"
														: "Nezdařilo se upravit údaje"));
							}
							close();
						} catch (FieldGroup.CommitException e) {
							Notification.show("   Chybná vstupní data\n\n   " + e.getCause().getMessage(),
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
				triggerComponent.setEnabled(true);
			}

		});

	}

	protected abstract void onSuccess();

}
