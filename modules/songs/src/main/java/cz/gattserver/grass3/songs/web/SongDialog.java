package cz.gattserver.grass3.songs.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import cz.gattserver.grass3.songs.facades.SongsService;
import cz.gattserver.grass3.songs.model.interfaces.SongTO;
import cz.gattserver.grass3.ui.components.SaveCloseLayout;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class SongDialog extends WebDialog {

	private static final long serialVersionUID = 6803519662032576371L;

	@Autowired
	private SongsService songsFacade;

	public SongDialog() {
		this(null);
	}

	public SongDialog(final SongTO originalTO) {
		setWidth("600px");

		SongTO formTO = new SongTO();
		formTO.setYear(0);

		Binder<SongTO> binder = new Binder<>(SongTO.class);
		binder.setBean(formTO);

		final TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(SongTO::getName, SongTO::setName);
		nameField.setWidthFull();
		addComponent(nameField);

		final TextField authorField = new TextField("Autor");
		binder.forField(authorField).bind(SongTO::getAuthor, SongTO::setAuthor);
		authorField.setWidthFull();

		final TextField yearField = new TextField("Rok");
		binder.forField(yearField).withConverter(new StringToIntegerConverter(null, "Rok musí být celé číslo"))
				.bind(SongTO::getYear, SongTO::setYear);
		yearField.setWidthFull();

		HorizontalLayout authorYearLayout = new HorizontalLayout(authorField, yearField);
		authorYearLayout.setSizeFull();
		add(authorYearLayout);

		final TextArea textField = new TextArea("Text");
		binder.forField(textField).asRequired().bind(SongTO::getText, SongTO::setText);
		textField.setWidthFull();
		textField.setHeight("500px");
		textField.getStyle().set("font-family", "monospace").set("font-size", "12px");
		add(textField);

		add(new SaveCloseLayout(event -> save(originalTO, binder), e -> close()));

		if (originalTO != null) {
			binder.readBean(originalTO);
			textField.setValue(songsFacade.breaklineToEol(originalTO.getText()));
		}
	}

	private void save(SongTO originalTO, Binder<SongTO> binder) {
		SongTO writeTO = originalTO == null ? new SongTO() : originalTO;
		if (binder.writeBeanIfValid(writeTO)) {
			try {
				onSave(writeTO);
				close();
			} catch (Exception ve) {
				new ErrorDialog("Uložení se nezdařilo").open();
			}
		}
	}

	protected abstract void onSave(SongTO to);

}
