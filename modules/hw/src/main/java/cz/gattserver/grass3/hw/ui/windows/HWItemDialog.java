package cz.gattserver.grass3.hw.ui.windows;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import cz.gattserver.grass3.hw.interfaces.HWItemState;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.ui.components.SaveCloseButtons;
import cz.gattserver.grass3.ui.util.TokenField;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.FieldUtils;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class HWItemDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient HWService hwService;

	public HWItemDialog(Long originalId) {
		init(originalId == null ? null : getHWService().getHWItem(originalId));
	}

	public HWItemDialog() {
		init(null);
	}

	public HWItemDialog(HWItemTO originalDTO) {
		init(originalDTO);
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	/**
	 * @param originalId
	 *            opravuji údaje existující položky, nebo vytvářím novou (
	 *            {@code null}) ?
	 */
	private void init(HWItemTO originalDTO) {
		HWItemTO formDTO = new HWItemTO();
		formDTO.setName("");
		formDTO.setPrice(new BigDecimal(0));
		formDTO.setWarrantyYears(0);
		formDTO.setState(HWItemState.NEW);
		formDTO.setPurchaseDate(LocalDate.now());

		FormLayout winLayout = new FormLayout();
		winLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("200px", 2));
		add(winLayout);
		winLayout.setWidth("500px");

		Binder<HWItemTO> binder = new Binder<>(HWItemTO.class);
		binder.setBean(formDTO);

		TextField nameField = new TextField("Název");
		nameField.setWidth("100%");
		nameField.addClassName("top-clean");
		binder.forField(nameField).asRequired("Název položky je povinný").bind("name");
		winLayout.add(nameField, 2);

		DatePicker purchaseDateField = new DatePicker("Získáno");
		purchaseDateField.setLocale(Locale.forLanguageTag("CS"));
		purchaseDateField.setSizeFull();
		binder.bind(purchaseDateField, "purchaseDate");
		winLayout.add(purchaseDateField);

		TextField priceField = new TextField("Cena");
		priceField.setSizeFull();
		binder.forField(priceField).withNullRepresentation("").withConverter(toModel -> {
			try {
				if (StringUtils.isBlank(toModel))
					return null;
				DecimalFormat df = new DecimalFormat();
				df.setParseBigDecimal(true);
				return (BigDecimal) df.parse(toModel);
			} catch (ParseException e1) {
				throw new IllegalArgumentException();
			}
		}, FieldUtils::formatMoney, "Cena musí být číslo").bind("price");
		winLayout.add(priceField);

		DatePicker destructionDateField = new DatePicker("Odepsáno");
		destructionDateField.setLocale(Locale.forLanguageTag("CS"));
		binder.bind(destructionDateField, "destructionDate");
		destructionDateField.setSizeFull();
		winLayout.add(destructionDateField);

		ComboBox<HWItemState> stateComboBox = new ComboBox<>("Stav", Arrays.asList(HWItemState.values()));
		stateComboBox.setWidth("100%");
		stateComboBox.setItemLabelGenerator(HWItemState::getName);
		binder.forField(stateComboBox).asRequired("Stav položky je povinný").bind("state");
		winLayout.add(stateComboBox);

		TextField warrantyYearsField = new TextField("Záruka (roky)");
		binder.forField(warrantyYearsField).withNullRepresentation("")
				.withConverter(new StringToIntegerConverter(null, "Záruka musí být celé číslo")).bind("warrantyYears");
		warrantyYearsField.setSizeFull();
		winLayout.add(warrantyYearsField);

		TextField supervizedForField = new TextField("Spravováno pro");
		supervizedForField.setWidth("100%");
		binder.bind(supervizedForField, "supervizedFor");
		winLayout.add(supervizedForField);

		Map<String, HWItemTypeTO> tokens = new HashMap<>();
		getHWService().getAllHWTypes().forEach(to -> tokens.put(to.getName(), to));

		TokenField keywords = new TokenField(tokens.keySet());
		keywords.setAllowNewItems(true);
		keywords.getInputField().setPlaceholder("klíčové slovo");

		if (originalDTO != null)
			keywords.setValues(originalDTO.getTypes());
		winLayout.add(keywords, 2);

		SaveCloseButtons buttons = new SaveCloseButtons(e -> {
			try {
				HWItemTO writeDTO = originalDTO == null ? new HWItemTO() : originalDTO;
				binder.writeBean(writeDTO);
				writeDTO.setTypes(keywords.getValues());
				writeDTO.setId(getHWService().saveHWItem(writeDTO));
				onSuccess(writeDTO);
				close();
			} catch (Exception ve) {
				new ErrorDialog("Uložení se nezdařilo").open();
			}
		}, e -> close());

		add(buttons);

		if (originalDTO != null)
			binder.readBean(originalDTO);
	}

	protected abstract void onSuccess(HWItemTO dto);

}
