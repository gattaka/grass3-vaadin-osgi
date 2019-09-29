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

import cz.gattserver.grass3.drinks.model.domain.MaltType;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;
import cz.gattserver.grass3.ui.util.RatingStars;

public abstract class BeerWindow extends DrinkWindow<BeerTO> {

	private static final long serialVersionUID = -3221345832430984490L;

	public BeerWindow(BeerTO to) {
		super(to);
	}

	public BeerWindow() {
		super();
	}

	@Override
	protected BeerTO createNewInstance() {
		BeerTO formTO = new BeerTO();
		formTO.setCountry("ČR");
		formTO.setMaltType(MaltType.BARLEY);
		return formTO;
	}

	@Override
	protected VerticalLayout createForm(Binder<BeerTO> binder) {
		TextField breweryField = new TextField("Pivovar");
		binder.forField(breweryField).asRequired().bind(BeerTO::getBrewery, BeerTO::setBrewery);

		TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(BeerTO::getName, BeerTO::setName);

		TextField countryField = new TextField("Země");
		binder.forField(countryField).asRequired().bind(BeerTO::getCountry, BeerTO::setCountry);

		RatingStars ratingStars = new RatingStars();
		binder.forField(ratingStars).asRequired().bind(BeerTO::getRating, BeerTO::setRating);

		HorizontalLayout line1Layout = new HorizontalLayout(breweryField, nameField, countryField, ratingStars);

		TextField categoryField = new TextField("Kategorie (APA, IPA, ...)");
		binder.forField(categoryField).asRequired().bind(BeerTO::getCategory, BeerTO::setCategory);

		TextField degreeField = new TextField("Stupně (°)");
		binder.forField(degreeField).withNullRepresentation("")
				.withConverter(new StringToDoubleConverter(null, "Stupně (°) musí být celé číslo") {
					private static final long serialVersionUID = -6368685797049169076L;

					@Override
					protected NumberFormat getFormat(Locale locale) {
						return NumberFormat.getNumberInstance(new Locale("cs", "CZ"));
					}
				}).bind(BeerTO::getDegrees, BeerTO::setDegrees);
		degreeField.setWidth("80px");

		TextField alcoholField = new TextField("Alkohol (%)");
		binder.forField(alcoholField).withNullRepresentation("")
				.withConverter(new StringToDoubleConverter(null, "Alkohol (%) musí být celé číslo") {
					private static final long serialVersionUID = 4910268168530306948L;

					@Override
					protected NumberFormat getFormat(Locale locale) {
						return NumberFormat.getNumberInstance(new Locale("cs", "CZ"));
					}
				}).bind(BeerTO::getAlcohol, BeerTO::setAlcohol);
		alcoholField.setWidth("80px");

		TextField ibuField = new TextField("Hořkost (IBU)");
		binder.forField(ibuField).withNullRepresentation("")
				.withConverter(new StringToIntegerConverter(null, "Hořkost (IBU) musí být celé číslo"))
				.bind(BeerTO::getIbu, BeerTO::setIbu);
		ibuField.setWidth("80px");

		ComboBox<MaltType> maltTypeField = new ComboBox<>("Typ sladu", Arrays.asList(MaltType.values()));
		maltTypeField.setItemLabelGenerator(MaltType::getCaption);
		binder.forField(maltTypeField).asRequired().bind(BeerTO::getMaltType, BeerTO::setMaltType);

		HorizontalLayout line2Layout = new HorizontalLayout(categoryField, degreeField, alcoholField, ibuField,
				maltTypeField);

		TextField maltsField = new TextField("Slady");
		binder.forField(maltsField).bind(BeerTO::getMalts, BeerTO::setMalts);
		maltsField.setWidth("290px");

		TextField hopsField = new TextField("Chmely");
		binder.forField(hopsField).bind(BeerTO::getHops, BeerTO::setHops);
		hopsField.setWidth("290px");

		HorizontalLayout line3Layout = new HorizontalLayout(maltsField, hopsField);

		TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(BeerTO::getDescription, BeerTO::setDescription);
		descriptionField.setWidth("600px");
		descriptionField.setHeight("200px");

		return new VerticalLayout(line1Layout, line2Layout, line3Layout, descriptionField);
	}

}
