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

import cz.gattserver.grass3.drinks.model.domain.WhiskeyType;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyTO;

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
		return new WhiskeyTO();
	}

	@Override
	protected void initFormTO(WhiskeyTO formTO) {
	}

	@Override
	protected VerticalLayout createForm(Binder<WhiskeyTO> binder) {
		final TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(WhiskeyTO::getName, WhiskeyTO::setName);

		final TextField countryField = new TextField("Země");
		binder.forField(countryField).asRequired().bind(WhiskeyTO::getCountry, WhiskeyTO::setCountry);

		final RatingStars ratingStars = new RatingStars();
		binder.forField(ratingStars).asRequired().bind(WhiskeyTO::getRating, WhiskeyTO::setRating);
		ratingStars.setAnimated(false);
		ratingStars.setCaption("Hodnocení");

		HorizontalLayout line1Layout = new HorizontalLayout(nameField, countryField, ratingStars);

		final TextField yearsField = new TextField("Stáří (roky)");
		binder.forField(yearsField).withNullRepresentation("").asRequired()
				.withConverter(new StringToIntegerConverter(null, "Stáří (roky) musí být celé číslo"))
				.bind(WhiskeyTO::getYears, WhiskeyTO::setYears);
		yearsField.setWidth("80px");

		final TextField alcoholField = new TextField("Alkohol (%)");
		binder.forField(alcoholField).withNullRepresentation("").asRequired()
				.withConverter(new StringToDoubleConverter(null, "Alkohol (%) musí být celé číslo") {
					private static final long serialVersionUID = 4910268168530306948L;

					@Override
					protected NumberFormat getFormat(Locale locale) {
						return NumberFormat.getNumberInstance(new Locale("cs", "CZ"));
					}
				}).bind(WhiskeyTO::getAlcohol, WhiskeyTO::setAlcohol);
		alcoholField.setWidth("80px");

		final ComboBox<WhiskeyType> maltTypeField = new ComboBox<>("Typ Whiskey", Arrays.asList(WhiskeyType.values()));
		maltTypeField.setItemCaptionGenerator(WhiskeyType::getCaption);
		binder.forField(maltTypeField).bind(WhiskeyTO::getWhiskeyType, WhiskeyTO::setWhiskeyType);

		HorizontalLayout line2Layout = new HorizontalLayout(yearsField, alcoholField, maltTypeField);

		final TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(WhiskeyTO::getDescription, WhiskeyTO::setDescription);
		descriptionField.setWidth("600px");
		descriptionField.setHeight("200px");

		return new VerticalLayout(line1Layout, line2Layout, descriptionField);
	}

}
