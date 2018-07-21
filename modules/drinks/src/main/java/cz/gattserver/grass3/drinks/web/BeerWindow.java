package cz.gattserver.grass3.drinks.web;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import org.vaadin.teemu.ratingstars.RatingStars;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.drinks.model.domain.MaltType;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;

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
		return new BeerTO();
	}

	@Override
	protected void initFormTO(BeerTO formTO) {
		formTO.setCountry("ČR");
		formTO.setMaltType(MaltType.BARLEY);
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
		ratingStars.setAnimated(false);
		ratingStars.setCaption("Hodnocení");

		HorizontalLayout line1Layout = new HorizontalLayout(breweryField, nameField, countryField, ratingStars);

		TextField categoryField = new TextField("Kategorie (APA, IPA, ...)");
		binder.forField(categoryField).asRequired().bind(BeerTO::getCategory, BeerTO::setCategory);

		TextField degreeField = new TextField("Stupně (°)");
		binder.forField(degreeField).withNullRepresentation("")
				.withConverter(new StringToIntegerConverter(null, "Stupně (°) musí být celé číslo"))
				.bind(BeerTO::getDegrees, BeerTO::setDegrees);
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
		maltTypeField.setItemCaptionGenerator(MaltType::getCaption);
		binder.forField(maltTypeField).bind(BeerTO::getMaltType, BeerTO::setMaltType);

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
