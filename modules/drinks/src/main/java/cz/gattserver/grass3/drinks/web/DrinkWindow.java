package cz.gattserver.grass3.drinks.web;

import java.util.Arrays;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.drinks.model.domain.DrinkType;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkTO;
import cz.gattserver.grass3.ui.components.CreateButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class DrinkWindow extends WebWindow {

	private static final long serialVersionUID = 6803519662032576371L;

	public DrinkWindow() {
		this(null);
	}

	protected abstract void onSave(DrinkTO to);

	public DrinkWindow(final DrinkTO originalTO) {
		super(originalTO == null ? "Založit" : "Upravit" + " nápoj");

		setWidth("600px");

		DrinkTO formTO = new DrinkTO();
		formTO.setRating(0);

		Binder<DrinkTO> binder = new Binder<>(DrinkTO.class);
		binder.setBean(formTO);

		final TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(DrinkTO::getName, DrinkTO::setName);
		nameField.setWidth("100%");
		addComponent(nameField);

		final ComboBox<DrinkType> typeField = new ComboBox<>("Typ", Arrays.asList(DrinkType.values()));
		binder.forField(typeField).bind(DrinkTO::getType, DrinkTO::setTyp);
		typeField.setWidth("100%");

		final TextField ratingField = new TextField("Hodnocení");
		binder.forField(ratingField).withConverter(new StringToIntegerConverter(null, "Hodnocení musí být celé číslo"))
				.bind(DrinkTO::getRating, DrinkTO::setRating);
		ratingField.setWidth("100%");

		HorizontalLayout authorYearLayout = new HorizontalLayout(typeField, ratingField);
		authorYearLayout.setSizeFull();
		addComponent(authorYearLayout);

		final TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(DrinkTO::getDescription, DrinkTO::setDescription);
		descriptionField.setWidth("100%");
		descriptionField.setHeight("500px");
		addComponent(descriptionField);

		Button b;
		if (originalTO != null)
			addComponent(b = new ModifyButton(event -> save(originalTO, binder)));
		else
			addComponent(b = new CreateButton(event -> save(originalTO, binder)));
		setComponentAlignment(b, Alignment.MIDDLE_CENTER);

		if (originalTO != null)
			binder.readBean(originalTO);
	}

	private void save(DrinkTO originalTO, Binder<DrinkTO> binder) {
		try {
			DrinkTO writeTO = originalTO == null ? new DrinkTO() : originalTO;
			binder.writeBean(writeTO);
			onSave(writeTO);
			close();
		} catch (ValidationException ve) {
			Notification.show(
					"Chybná vstupní data\n\n   " + ve.getValidationErrors().iterator().next().getErrorMessage(),
					Notification.Type.ERROR_MESSAGE);
		} catch (Exception ve) {
			UI.getCurrent().addWindow(new ErrorWindow("Uložení se nezdařilo"));
		}
	}

}
