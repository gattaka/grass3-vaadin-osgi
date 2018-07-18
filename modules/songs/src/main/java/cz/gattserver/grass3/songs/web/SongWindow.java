package cz.gattserver.grass3.songs.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.songs.facades.SongsFacade;
import cz.gattserver.grass3.songs.model.interfaces.SongTO;
import cz.gattserver.grass3.ui.components.CreateButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class SongWindow extends WebWindow {

	private static final long serialVersionUID = 6803519662032576371L;

	@Autowired
	private SongsFacade songsFacade;

	public SongWindow() {
		this(null);
	}

	protected abstract void onSave(SongTO to);

	public SongWindow(final SongTO originalTO) {
		super(originalTO == null ? "Založit" : "Upravit" + " písničku");

		setWidth("600px");

		SongTO formTO = new SongTO();
		formTO.setYear(0);

		Binder<SongTO> binder = new Binder<>(SongTO.class);
		binder.setBean(formTO);

		final TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(SongTO::getName, SongTO::setName);
		nameField.setWidth("100%");
		addComponent(nameField);

		final TextField authorField = new TextField("Autor");
		binder.forField(authorField).bind(SongTO::getAuthor, SongTO::setAuthor);
		authorField.setWidth("100%");

		final TextField yearField = new TextField("Rok");
		binder.forField(yearField).withConverter(new StringToIntegerConverter(null, "Rok musí být celé číslo"))
				.bind(SongTO::getYear, SongTO::setYear);
		yearField.setWidth("100%");

		HorizontalLayout authorYearLayout = new HorizontalLayout(authorField, yearField);
		authorYearLayout.setSizeFull();
		addComponent(authorYearLayout);

		final TextArea textField = new TextArea("Text");
		binder.forField(textField).asRequired().bind(SongTO::getText, SongTO::setText);
		textField.setWidth("100%");
		textField.setHeight("500px");
		addComponent(textField);

		Button b;
		if (originalTO != null)
			addComponent(b = new ModifyButton(event -> save(originalTO, binder)));
		else
			addComponent(b = new CreateButton(event -> save(originalTO, binder)));
		setComponentAlignment(b, Alignment.MIDDLE_CENTER);

		if (originalTO != null) {
			binder.readBean(originalTO);
			textField.setValue(songsFacade.breaklineToEol(originalTO.getText()));
		}
	}

	private void save(SongTO originalTO, Binder<SongTO> binder) {
		try {
			SongTO writeTO = originalTO == null ? new SongTO() : originalTO;
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
