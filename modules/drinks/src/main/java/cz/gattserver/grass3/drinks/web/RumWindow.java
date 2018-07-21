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

import cz.gattserver.grass3.drinks.model.domain.RumType;
import cz.gattserver.grass3.drinks.model.interfaces.RumTO;

public abstract class RumWindow extends DrinkWindow<RumTO> {

	private static final long serialVersionUID = 6803519662032576371L;

	public RumWindow(RumTO to) {
		super(to);
	}

	public RumWindow() {
		super();
	}

	@Override
	protected RumTO createNewInstance() {
		return new RumTO();
	}

	@Override
	protected void initFormTO(RumTO formTO) {
		formTO.setRumType(RumType.DARK);
	}

	@Override
	protected VerticalLayout createForm(Binder<RumTO> binder) {
		final TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(RumTO::getName, RumTO::setName);

		final TextField countryField = new TextField("Země");
		binder.forField(countryField).asRequired().bind(RumTO::getCountry, RumTO::setCountry);

		final RatingStars ratingStars = new RatingStars();
		binder.forField(ratingStars).asRequired().bind(RumTO::getRating, RumTO::setRating);
		ratingStars.setAnimated(false);
		ratingStars.setCaption("Hodnocení");

		HorizontalLayout line1Layout = new HorizontalLayout(nameField, countryField, ratingStars);

		final TextField yearsField = new TextField("Stáří (roky)");
		binder.forField(yearsField).withNullRepresentation("")
				.withConverter(new StringToIntegerConverter(null, "Stáří (roky) musí být celé číslo"))
				.bind(RumTO::getYears, RumTO::setYears);
		yearsField.setWidth("80px");

		final TextField alcoholField = new TextField("Alkohol (%)");
		binder.forField(alcoholField).withNullRepresentation("").asRequired()
				.withConverter(new StringToDoubleConverter(null, "Alkohol (%) musí být celé číslo") {
					private static final long serialVersionUID = 4910268168530306948L;

					@Override
					protected NumberFormat getFormat(Locale locale) {
						return NumberFormat.getNumberInstance(new Locale("cs", "CZ"));
					}
				}).bind(RumTO::getAlcohol, RumTO::setAlcohol);
		alcoholField.setWidth("80px");

		final ComboBox<RumType> maltTypeField = new ComboBox<>("Typ rumu", Arrays.asList(RumType.values()));
		maltTypeField.setItemCaptionGenerator(RumType::getCaption);
		binder.forField(maltTypeField).asRequired().bind(RumTO::getRumType, RumTO::setRumType);

		HorizontalLayout line2Layout = new HorizontalLayout(yearsField, alcoholField, maltTypeField);

		final TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(RumTO::getDescription, RumTO::setDescription);
		descriptionField.setWidth("600px");
		descriptionField.setHeight("200px");

		return new VerticalLayout(line1Layout, line2Layout, descriptionField);
	}

}
