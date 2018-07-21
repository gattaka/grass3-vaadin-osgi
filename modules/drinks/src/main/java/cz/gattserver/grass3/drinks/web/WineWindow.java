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

import cz.gattserver.grass3.drinks.model.domain.WineType;
import cz.gattserver.grass3.drinks.model.interfaces.WineTO;

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
		return new WineTO();
	}

	@Override
	protected void initFormTO(WineTO formTO) {
	}

	@Override
	protected VerticalLayout createForm(Binder<WineTO> binder) {
		final TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(WineTO::getName, WineTO::setName);

		final TextField countryField = new TextField("Země");
		binder.forField(countryField).asRequired().bind(WineTO::getCountry, WineTO::setCountry);

		final RatingStars ratingStars = new RatingStars();
		binder.forField(ratingStars).asRequired().bind(WineTO::getRating, WineTO::setRating);
		ratingStars.setAnimated(false);
		ratingStars.setCaption("Hodnocení");

		HorizontalLayout line1Layout = new HorizontalLayout(nameField, countryField, ratingStars);

		final TextField wineryField = new TextField("Vinařství");
		binder.forField(wineryField).asRequired().bind(WineTO::getWinery, WineTO::setWinery);
		wineryField.setWidth("80px");

		final TextField yearsField = new TextField("Roky");
		binder.forField(yearsField).withNullRepresentation("").asRequired()
				.withConverter(new StringToIntegerConverter(null, "Rok musí být celé číslo"))
				.bind(WineTO::getYear, WineTO::setYear);
		yearsField.setWidth("80px");

		final TextField alcoholField = new TextField("Alkohol (%)");
		binder.forField(alcoholField).withNullRepresentation("")
				.withConverter(new StringToDoubleConverter(null, "Alkohol (%) musí být celé číslo") {
					private static final long serialVersionUID = 4910268168530306948L;

					@Override
					protected NumberFormat getFormat(Locale locale) {
						return NumberFormat.getNumberInstance(new Locale("cs", "CZ"));
					}
				}).bind(WineTO::getAlcohol, WineTO::setAlcohol);
		alcoholField.setWidth("80px");

		final ComboBox<WineType> maltTypeField = new ComboBox<>("Typ vína", Arrays.asList(WineType.values()));
		maltTypeField.setItemCaptionGenerator(WineType::getCaption);
		binder.forField(maltTypeField).bind(WineTO::getWineType, WineTO::setWineType);

		HorizontalLayout line2Layout = new HorizontalLayout(yearsField, alcoholField, maltTypeField);

		final TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(WineTO::getDescription, WineTO::setDescription);
		descriptionField.setWidth("600px");
		descriptionField.setHeight("200px");

		return new VerticalLayout(line1Layout, line2Layout, descriptionField);
	}

}
