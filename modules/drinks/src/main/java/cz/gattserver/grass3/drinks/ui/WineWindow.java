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

import cz.gattserver.grass3.drinks.model.domain.WineType;
import cz.gattserver.grass3.drinks.model.interfaces.WineTO;
import cz.gattserver.grass3.ui.util.RatingStars;

public abstract class WineWindow extends DrinkWindow<WineTO> {

	private static final long serialVersionUID = 6803519662032576371L;

	public WineWindow(WineTO to) {
		super(to);
	}

	public WineWindow() {
		super();
	}

	@Override
	protected WineTO createNewInstance() {
		WineTO formTO = new WineTO();
		formTO.setYear(0);
		return formTO;
	}

	@Override
	protected VerticalLayout createForm(Binder<WineTO> binder) {

		TextField wineryField = new TextField("Vinařství");
		binder.forField(wineryField).asRequired().bind(WineTO::getWinery, WineTO::setWinery);
		wineryField.setWidth("80px");

		TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(WineTO::getName, WineTO::setName);

		TextField countryField = new TextField("Země");
		binder.forField(countryField).asRequired().bind(WineTO::getCountry, WineTO::setCountry);

		RatingStars ratingStars = new RatingStars();
		binder.forField(ratingStars).asRequired().bind(WineTO::getRating, WineTO::setRating);

		HorizontalLayout line1Layout = new HorizontalLayout(wineryField, nameField, countryField, ratingStars);

		TextField yearsField = new TextField("Roky");
		binder.forField(yearsField).withConverter(new StringToIntegerConverter(null, "Rok musí být celé číslo"))
				.asRequired(new IntegerRangeValidator("Rok vína je mimo rozsah (1000-3000)", 1000, 3000))
				.bind(WineTO::getYear, WineTO::setYear);
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
				.bind(WineTO::getAlcohol, WineTO::setAlcohol);
		alcoholField.setWidth("80px");

		ComboBox<WineType> wineTypeField = new ComboBox<>("Typ vína", Arrays.asList(WineType.values()));
		wineTypeField.setItemLabelGenerator(WineType::getCaption);
		binder.forField(wineTypeField).asRequired().bind(WineTO::getWineType, WineTO::setWineType);

		HorizontalLayout line2Layout = new HorizontalLayout(yearsField, alcoholField, wineTypeField);

		TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(WineTO::getDescription, WineTO::setDescription);
		descriptionField.setWidth("600px");
		descriptionField.setHeight("200px");

		return new VerticalLayout(line1Layout, line2Layout, descriptionField);
	}

}
