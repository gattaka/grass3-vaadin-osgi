package cz.gattserver.grass3.songs.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.songs.facades.SongsFacade;
import cz.gattserver.grass3.songs.model.dto.SongDTO;
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

	protected abstract void onSave(SongDTO dto);

	public SongWindow(final SongDTO originalDTO) {
		super(originalDTO == null ? "Založit" : "Upravit" + " písničku");

		setWidth("600px");

		SongDTO formDTO = new SongDTO();

		Binder<SongDTO> binder = new Binder<>(SongDTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(SongDTO::getName, SongDTO::setName);
		nameField.setWidth("100%");
		addComponent(nameField);

		final TextField authorField = new TextField("Autor");
		binder.forField(authorField).bind(SongDTO::getAuthor, SongDTO::setAuthor);
		authorField.setWidth("100%");
		addComponent(authorField);

		final TextField yearField = new TextField("Rok");
		binder.forField(yearField).withConverter(new StringToIntegerConverter(null, "Rok musí být celé číslo"))
				.bind(SongDTO::getYear, SongDTO::setYear);
		yearField.setWidth("100%");
		addComponent(yearField);

		final TextArea textField = new TextArea("Text");
		binder.forField(textField).asRequired().bind(SongDTO::getText, SongDTO::setText);
		textField.setWidth("100%");
		textField.setHeight("500px");
		addComponent(textField);

		Button b;
		if (originalDTO != null)
			addComponent(b = new ModifyButton(event -> save(originalDTO, binder)));
		else
			addComponent(b = new CreateButton(event -> save(originalDTO, binder)));
		setComponentAlignment(b, Alignment.MIDDLE_CENTER);

		if (originalDTO != null) {
			binder.readBean(originalDTO);
			textField.setValue(songsFacade.breaklineToEol(originalDTO.getDescription()));
		}
	}

	private void save(SongDTO originalDTO, Binder<SongDTO> binder) {
		try {
			SongDTO writeDTO = originalDTO == null ? new SongDTO() : originalDTO;
			binder.writeBean(writeDTO);
			onSave(writeDTO);
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
