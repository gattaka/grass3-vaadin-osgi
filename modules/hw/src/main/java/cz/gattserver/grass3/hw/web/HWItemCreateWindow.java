package cz.gattserver.grass3.hw.web;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;

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

	public HWItemCreateWindow(Long originalId) {
		init(hwFacade.getHWItem(originalId));
	}

	public HWItemCreateWindow() {
		init(null);
	}

	public HWItemCreateWindow(HWItemDTO originalDTO) {
		init(originalDTO);
	}

	/**
	 * @param triggerComponent
	 *            volající komponenta (ta, která má být po dobu zobrazení okna
	 *            zablokována)
	 * @param originalId
	 *            opravuji údaje existující položky, nebo vytvářím novou (
	 *            {@code null}) ?
	 */
	private void init(HWItemDTO originalDTO) {
		setCaption(originalDTO == null ? "Založení nové položky HW" : "Oprava údajů existující položky HW");

		HWItemDTO formDTO = new HWItemDTO();
		formDTO.setName("");
		formDTO.setPrice(new BigDecimal(0));
		formDTO.setWarrantyYears(0);

		GridLayout winLayout = new GridLayout(2, 5);
		layout.addComponent(winLayout);
		winLayout.setWidth("400px");
		winLayout.setSpacing(true);

		Binder<HWItemDTO> binder = new Binder<>(HWItemDTO.class);
		binder.setBean(formDTO);

		TextField nameField = new TextField("Název");
		nameField.setWidth("100%");
		binder.forField(nameField).asRequired("Název položky je povinný").bind("name");
		winLayout.addComponent(nameField, 0, 0, 1, 0);

		DateField purchaseDateField = new DateField("Získáno");
		purchaseDateField.setDateFormat("dd.MM.yyyy");
		purchaseDateField.setLocale(Locale.forLanguageTag("CS"));
		purchaseDateField.setSizeFull();
		binder.bind(purchaseDateField, "purchaseDate");
		winLayout.addComponent(purchaseDateField, 0, 1);

		TextField priceField = new TextField("Cena");
		priceField.setSizeFull();
		binder.forField(priceField).withConverter(toModel -> {
			try {
				if (StringUtils.isBlank(toModel))
					return null;
				DecimalFormat df = new DecimalFormat();
				df.setParseBigDecimal(true);
				return (BigDecimal) df.parse(toModel);
			} catch (ParseException e1) {
				throw new IllegalArgumentException();
			}
		}, toPresentation -> FieldUtils.formatMoney(toPresentation), "Cena musí být číslo").bind("price");
		winLayout.addComponent(priceField, 1, 1);

		DateField destructionDateField = new DateField("Odepsáno");
		destructionDateField.setDateFormat("dd.MM.yyyy");
		destructionDateField.setLocale(Locale.forLanguageTag("CS"));
		binder.bind(destructionDateField, "destructionDate");
		destructionDateField.setSizeFull();
		winLayout.addComponent(destructionDateField, 0, 2);

		ComboBox<HWItemState> stateComboBox = new ComboBox<>("Stav", Arrays.asList(HWItemState.values()));
		stateComboBox.setWidth("100%");
		stateComboBox.setEmptySelectionAllowed(false);
		stateComboBox.setItemCaptionGenerator(item -> item.getName());
		binder.forField(stateComboBox).asRequired("Stav položky je povinný").bind("state");
		winLayout.addComponent(stateComboBox, 1, 2);

		TextField warrantyYearsField = new TextField("Záruka (roky)");
		binder.forField(warrantyYearsField)
				.withConverter(new StringToIntegerConverter(null, "Záruka musí být celé číslo")).bind("warrantyYears");
		warrantyYearsField.setSizeFull();
		winLayout.addComponent(warrantyYearsField, 0, 3);

		TextField supervizedForField = new TextField("Spravováno pro");
		supervizedForField.setWidth("100%");
		binder.bind(supervizedForField, "supervizedFor");
		winLayout.addComponent(supervizedForField, 1, 3);

		Set<HWItemTypeDTO> types = hwFacade.getAllHWTypes();
		final TwinColSelect<HWItemTypeDTO> typeSelect = new TwinColSelect<>("Typy", types);
		typeSelect.setWidth("100%");
		typeSelect.setRows(7);
		typeSelect.setItemCaptionGenerator(HWItemTypeDTO::getName);
		binder.bind(typeSelect, "types");
		winLayout.addComponent(typeSelect, 0, 4, 1, 4);

		Button createBtn;
		layout.addComponent(createBtn = new Button("Uložit", e -> {
			try {
				HWItemDTO writeDTO = originalDTO == null ? new HWItemDTO() : originalDTO;
				binder.writeBean(writeDTO);
				hwFacade.saveHWItem(writeDTO);
				onSuccess();
				close();
			} catch (ValidationException ve) {
				Notification.show(
						"Chybná vstupní data\n\n   " + ve.getValidationErrors().iterator().next().getErrorMessage(),
						Notification.Type.ERROR_MESSAGE);
			} catch (Exception ve) {
				UI.getCurrent().addWindow(new ErrorWindow("Uložení se nezdařilo"));
			}
		}));
		layout.setComponentAlignment(createBtn, Alignment.BOTTOM_RIGHT);
		setContent(layout);

		if (originalDTO != null)
			binder.readBean(originalDTO);
	}

	protected abstract void onSuccess();

}
