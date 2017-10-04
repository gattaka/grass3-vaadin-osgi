package cz.gattserver.grass3.hw.web;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.hw.dto.HWItemDTO;
import cz.gattserver.grass3.hw.dto.HWItemState;
import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.facade.HWFacade;
import cz.gattserver.web.common.ui.FieldUtils;
import cz.gattserver.web.common.window.ErrorWindow;
import cz.gattserver.web.common.window.WebWindow;

public abstract class HWItemCreateWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	@Autowired
	private HWFacade hwFacade;

	private HWItemDTO hwItemDTO;

	/**
	 * @param triggerComponent
	 *            volající komponenta (ta, která má být po dobu zobrazení okna
	 *            zablokována)
	 * @param fixItemId
	 *            opravuji údaje existující položky, nebo vytvářím novou (
	 *            {@code null}) ?
	 */
	public HWItemCreateWindow(final Component triggerComponent, final Long fixItemId) {
		super(fixItemId == null ? "Založení nové položky HW" : "Oprava údajů existující položky HW");

		triggerComponent.setEnabled(false);

		if (fixItemId != null) {
			hwItemDTO = hwFacade.getHWItem(fixItemId);
		} else {
			hwItemDTO = new HWItemDTO();
			hwItemDTO.setName("");
			hwItemDTO.setPrice(new BigDecimal(0));
			hwItemDTO.setWarrantyYears(0);
		}

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		GridLayout winLayout = new GridLayout(2, 5);
		layout.addComponent(winLayout);
		winLayout.setWidth("400px");
		winLayout.setSpacing(true);

		Binder<HWItemDTO> binder = new Binder<>();

		TextField nameField = new TextField("Název");
		nameField.setWidth("100%");
		binder.bind(nameField, HWItemDTO::getName, HWItemDTO::setName);
		winLayout.addComponent(nameField, 0, 0, 1, 0);

		DateField purchaseDateField = new DateField("Získáno");
		purchaseDateField.setDateFormat("dd.MM.yyyy");
		purchaseDateField.setLocale(Locale.forLanguageTag("CS"));
		purchaseDateField.setSizeFull();
		binder.bind(purchaseDateField, HWItemDTO::getPurchaseDate, HWItemDTO::setPurchaseDate);
		winLayout.addComponent(purchaseDateField, 0, 1);

		TextField priceField = new TextField("Cena");
		priceField.setSizeFull();
		binder.forField(priceField)
				.withConverter(toModel -> new BigDecimal(toModel),
						toPresentation -> FieldUtils.formatMoney(toPresentation), "Cena musí být číslo")
				.bind(HWItemDTO::getPrice, HWItemDTO::setPrice);
		winLayout.addComponent(priceField, 1, 1);

		DateField destructionDateField = new DateField("Odepsáno");
		destructionDateField.setDateFormat("dd.MM.yyyy");
		destructionDateField.setLocale(Locale.forLanguageTag("CS"));
		binder.bind(destructionDateField, HWItemDTO::getDestructionDate, HWItemDTO::setDestructionDate);
		destructionDateField.setSizeFull();
		winLayout.addComponent(destructionDateField, 0, 2);

		ComboBox<HWItemState> stateComboBox = new ComboBox<>("Stav", Arrays.asList(HWItemState.values()));
		stateComboBox.setWidth("100%");
		stateComboBox.setEmptySelectionAllowed(false);
		stateComboBox.setItemCaptionGenerator(item -> item.name());

		winLayout.addComponent(stateComboBox, 1, 1);

		TextField warrantyYearsField = new TextField("Záruka (roky)");
		// fieldGroup.bind(warrantyYearsField, "warrantyYears");
		warrantyYearsField.setSizeFull();
		winLayout.addComponent(warrantyYearsField, 1, 2);

		TextField supervizedForField = new TextField("Spravováno pro");
		// supervizedForField.setImmediate(true);
		supervizedForField.setWidth("100%");
		// supervizedForField.setNullRepresentation("");
		// fieldGroup.bind(supervizedForField, "supervizedFor");
		winLayout.addComponent(supervizedForField, 0, 3, 1, 3);

		Set<HWItemTypeDTO> types = hwFacade.getAllHWTypes();
		final TwinColSelect<HWItemTypeDTO> typeSelect = new TwinColSelect<>("Typy", types);
		typeSelect.setWidth("100%");
		typeSelect.setRows(7);
		typeSelect.setItemCaptionGenerator(HWItemTypeDTO::getName);
		binder.bind(typeSelect, HWItemDTO::getTypes, HWItemDTO::setTypes);
		winLayout.addComponent(typeSelect, 0, 4, 1, 4);

		Button createBtn;
		layout.addComponent(createBtn = new Button("Uložit", e -> {
			BinderValidationStatus<HWItemDTO> status = binder.validate();
			if (status.isOk()) {
				if (hwFacade.saveHWItem(hwItemDTO)) {
					onSuccess();
				} else {
					UI.getCurrent().addWindow(new ErrorWindow("Uložení se nezdařilo"));
				}
				close();
			} else {
				Notification.show("Chybná vstupní data\n\n   " + status.getValidationErrors().get(0).getErrorMessage(),
						Notification.Type.TRAY_NOTIFICATION);
			}

		}));
		layout.setComponentAlignment(createBtn, Alignment.BOTTOM_RIGHT);

		setContent(layout);

		addCloseListener(e -> triggerComponent.setEnabled(true));
	}

	protected abstract void onSuccess();

}
