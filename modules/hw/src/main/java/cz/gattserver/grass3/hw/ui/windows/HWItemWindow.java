package cz.gattserver.grass3.hw.ui.windows;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fo0.advancedtokenfield.main.AdvancedTokenField;
import com.fo0.advancedtokenfield.main.Token;
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
import com.vaadin.ui.UI;

import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.interfaces.HWItemState;
import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.FieldUtils;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class HWItemWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient HWService hwService;

	public HWItemWindow(Long originalId) {
		init(originalId == null ? null : getHWService().getHWItem(originalId));
	}

	public HWItemWindow() {
		init(null);
	}

	public HWItemWindow(HWItemTO originalDTO) {
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
		setCaption(originalDTO == null ? "Založení nové položky HW" : "Oprava údajů existující položky HW");

		HWItemTO formDTO = new HWItemTO();
		formDTO.setName("");
		formDTO.setPrice(new BigDecimal(0));
		formDTO.setWarrantyYears(0);
		formDTO.setState(HWItemState.NEW);
		formDTO.setPurchaseDate(LocalDate.now());

		GridLayout winLayout = new GridLayout(2, 5);
		layout.addComponent(winLayout);
		winLayout.setWidth("400px");
		winLayout.setSpacing(true);

		Binder<HWItemTO> binder = new Binder<>(HWItemTO.class);
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
		}, FieldUtils::formatMoney, "Cena musí být číslo").bind("price");
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
		stateComboBox.setItemCaptionGenerator(HWItemState::getName);
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

		AdvancedTokenField keywords = new AdvancedTokenField();
		keywords.isEnabled();
		keywords.setAllowNewItems(true);
		keywords.getInputField().setPlaceholder("klíčové slovo");

		Set<HWItemTypeTO> contentTypes = getHWService().getAllHWTypes();
		contentTypes.forEach(t -> {
			Token to = new Token(t.getName());
			keywords.addTokenToInputField(to);
		});

		if (originalDTO != null)
			for (String typeName : originalDTO.getTypes())
				keywords.addToken(new Token(typeName));
		winLayout.addComponent(keywords, 0, 4, 1, 4);

		Button createBtn;
		createBtn = new Button("Uložit", e -> {
			try {
				HWItemTO writeDTO = originalDTO == null ? new HWItemTO() : originalDTO;
				binder.writeBean(writeDTO);
				Set<String> tokens = new HashSet<>();
				keywords.getTokens().forEach(t -> tokens.add(t.getValue()));
				writeDTO.setTypes(tokens);
				writeDTO.setId(getHWService().saveHWItem(writeDTO));
				onSuccess(writeDTO);
				close();
			} catch (ValidationException ve) {
				Notification.show(
						"Chybná vstupní data\n\n   " + ve.getValidationErrors().iterator().next().getErrorMessage(),
						Notification.Type.ERROR_MESSAGE);
			} catch (Exception ve) {
				UI.getCurrent().addWindow(new ErrorWindow("Uložení se nezdařilo"));
			}
		});
		layout.addComponent(createBtn);
		layout.setComponentAlignment(createBtn, Alignment.BOTTOM_RIGHT);
		setContent(layout);

		if (originalDTO != null)
			binder.readBean(originalDTO);
		
		removeAllCloseShortcuts();
	}

	protected abstract void onSuccess(HWItemTO dto);

}
