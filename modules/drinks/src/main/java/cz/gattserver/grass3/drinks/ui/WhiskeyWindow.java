package cz.gattserver.grass3.drinks.ui;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.IntegerRangeValidator;

import cz.gattserver.grass3.drinks.model.domain.WhiskeyType;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyTO;
import cz.gattserver.grass3.ui.util.RatingStars;

public abstract class WhiskeyWindow extends DrinkWindow<WhiskeyTO> {

	private static final long serialVersionUID = 6803519662032576371L;

	public WhiskeyWindow(WhiskeyTO to) {
		super(to);
	}

	public WhiskeyWindow() {
		super();
	}

	@Override
	protected WhiskeyTO createNewInstance() {
		WhiskeyTO formTO = new WhiskeyTO();
		formTO.setAlcohol(0d);
		formTO.setYears(0);
		return formTO;
	}

	@Override
	protected VerticalLayout createForm(Binder<WhiskeyTO> binder) {
		TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(WhiskeyTO::getName, WhiskeyTO::setName);

		TextField countryField = new TextField("Země");
		binder.forField(countryField).asRequired().bind(WhiskeyTO::getCountry, WhiskeyTO::setCountry);

		RatingStars ratingStars = new RatingStars();
		binder.forField(ratingStars).asRequired().bind(WhiskeyTO::getRating, WhiskeyTO::setRating);

		HorizontalLayout line1Layout = new HorizontalLayout(nameField, countryField, ratingStars);

		TextField yearsField = new TextField("Stáří (roky)");
		binder.forField(yearsField)
				.withConverter(new StringToIntegerConverter(null, "Stáří (roky) musí být celé číslo"))
				.asRequired(new IntegerRangeValidator("Stáří je mimo rozsah (1-100)", 1, 100))
				.bind(WhiskeyTO::getYears, WhiskeyTO::setYears);
		yearsField.setWidth("80px");

		TextField alcoholField = new TextField("Alkohol (%)");
		binder.forField(alcoholField)
				.withConverter(new StringToDoubleConverter(null, "Alkohol (%) musí být celé číslo") {
					private static final long serialVersionUID = 4910268168530306948L;

					@Override
					protected NumberFormat getFormat(Locale locale) {
						return NumberFormat.getNumberInstance(new Locale("cs", "CZ"));
					}
				}).asRequired(new DoubleRangeValidator("Obsah alkoholu je mimo rozsah (1-100)", 1d, 100d))
				.bind(WhiskeyTO::getAlcohol, WhiskeyTO::setAlcohol);
		alcoholField.setWidth("80px");

		ComboBox<WhiskeyType> whiskeyTypeField = new ComboBox<>("Typ Whiskey", Arrays.asList(WhiskeyType.values()));
		whiskeyTypeField.setItemLabelGenerator(WhiskeyType::getCaption);
		binder.forField(whiskeyTypeField).asRequired().bind(WhiskeyTO::getWhiskeyType, WhiskeyTO::setWhiskeyType);

		HorizontalLayout line2Layout = new HorizontalLayout(yearsField, alcoholField, whiskeyTypeField);

		TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(WhiskeyTO::getDescription, WhiskeyTO::setDescription);
		descriptionField.setWidth("600px");
		descriptionField.setHeight("200px");

		return new VerticalLayout(line1Layout, line2Layout, descriptionField);
	}

}
