package cz.gattserver.grass3.songs.web;

import java.util.Arrays;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import cz.gattserver.grass3.songs.model.domain.Instrument;
import cz.gattserver.grass3.songs.model.interfaces.ChordTO;
import cz.gattserver.grass3.ui.components.SaveCloseButtons;
import cz.gattserver.web.common.ui.Breakline;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class ChordDialog extends WebDialog {

	private static final long serialVersionUID = 6803519662032576371L;

	public ChordDialog() {
		this(null);
	}

	public ChordDialog(final ChordTO originalDTO) {
		this(originalDTO, false);
	}

	public ChordDialog(final ChordTO originalDTO, boolean copy) {
		super(originalDTO == null || copy ? "Založit" : "Upravit" + " akord");

		setWidth("600px");

		ChordTO formTO = new ChordTO();
		formTO.setInstrument(Instrument.GUITAR);

		Binder<ChordTO> binder = new Binder<>(ChordTO.class);
		binder.setBean(formTO);

		final TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(ChordTO::getName, ChordTO::setName);
		nameField.setWidth("100%");
		addComponent(nameField);

		final ComboBox<Instrument> instrumentField = new ComboBox<>("Nástroj", Arrays.asList(Instrument.values()));
		instrumentField.setItemLabelGenerator(Instrument::getCaption);
		instrumentField.setClearButtonVisible(true);
		instrumentField.setWidth("100%");
		addComponent(instrumentField);

		VerticalLayout chordDescriptionLayout = new VerticalLayout();
		chordDescriptionLayout.setMargin(false);
		addComponent(chordDescriptionLayout);
		instrumentField.addValueChangeListener(
				e -> refreshDescriptionLayout(binder, originalDTO, formTO, chordDescriptionLayout, e.getValue()));
		addComponent(chordDescriptionLayout);

		binder.forField(instrumentField).asRequired().bind(ChordTO::getInstrument, ChordTO::setInstrument);

		add(new SaveCloseButtons(e -> save(binder), e -> close()));

		if (originalDTO != null) {
			binder.readBean(originalDTO);
			formTO.setConfiguration(originalDTO.getConfiguration());
			formTO.setId(originalDTO.getId());
			if (copy)
				binder.getBean().setId(null);
		}
	}

	private void refreshDescriptionLayout(Binder<ChordTO> binder, ChordTO originalDTO, ChordTO formTO,
			VerticalLayout chordDescriptionLayout, Instrument instrument) {
		chordDescriptionLayout.removeAll();
		switch (instrument) {
		case GUITAR:
			refreshDescriptionLayoutAsGuitar(binder, originalDTO, formTO, chordDescriptionLayout);
			break;
		default:
			break;
		}
	}

	private void refreshDescriptionLayoutAsGuitar(Binder<ChordTO> binder, ChordTO originalDTO, ChordTO formTO,
			VerticalLayout chordDescriptionLayout) {
		String[] stringsLabel = new String[] { "E", "a", "d", "g", "h", "e" };

		Div layout = new Div();
		chordDescriptionLayout.add(layout);
		int rows = 17;
		int cols = 6;
		for (int row = 0; row < rows; row++) {
			if (row % 2 != 0) {
				Hr hrLabel = new Hr();
				hrLabel.setWidth("100%");
				layout.add(hrLabel);
			} else {
				for (int col = 0; col < cols; col++) {
					if (row == 0) {
						String val = stringsLabel[col];
						layout.add(new Span(val));
					} else {
						Checkbox cb = new Checkbox();
						layout.add(cb);
						long bitMask = 1L << ((row / 2 - 1) * cols + col);
						if (originalDTO != null)
							cb.setValue((originalDTO.getConfiguration().longValue() & bitMask) > 0);
						cb.addValueChangeListener(val -> {
							if (val.getValue())
								formTO.setConfiguration(formTO.getConfiguration().longValue() | bitMask);
							else
								formTO.setConfiguration(formTO.getConfiguration().longValue() & ~bitMask);
						});
					}
					layout.add(new Breakline());
				}
			}
		}
	}

	private void save(Binder<ChordTO> binder) {
		try {
			ChordTO writeTO = new ChordTO();
			binder.writeBean(writeTO);
			writeTO.setConfiguration(binder.getBean().getConfiguration());
			writeTO.setId(binder.getBean().getId());
			onSave(writeTO);
			close();
		} catch (ValidationException ve) {
			new ErrorDialog("Chybná vstupní data\n\n   " + ve.getValidationErrors().iterator().next().getErrorMessage())
					.open();
		} catch (Exception ve) {
			new ErrorDialog("Uložení se nezdařilo").open();
		}
	}

	protected abstract void onSave(ChordTO to);

}
