package cz.gattserver.grass3.songs.web;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.songs.model.domain.Instrument;
import cz.gattserver.grass3.songs.model.dto.ChordTO;
import cz.gattserver.grass3.ui.components.CreateButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class ChordWindow extends WebWindow {

	private static final long serialVersionUID = 6803519662032576371L;

	public ChordWindow() {
		this(null);
	}

	protected abstract void onSave(ChordTO to);

	public ChordWindow(final ChordTO originalDTO) {
		super(originalDTO == null ? "Založit" : "Upravit" + " akord");

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
		instrumentField.setItemCaptionGenerator(Instrument::getCaption);
		instrumentField.setEmptySelectionAllowed(false);
		instrumentField.setWidth("100%");
		addComponent(instrumentField);

		VerticalLayout chordDescriptionLayout = new VerticalLayout();
		chordDescriptionLayout.setMargin(false);
		addComponent(chordDescriptionLayout);
		instrumentField.addValueChangeListener(
				e -> refreshDescriptionLayout(originalDTO, formTO, chordDescriptionLayout, e.getValue()));
		addComponent(chordDescriptionLayout);

		binder.forField(instrumentField).asRequired().bind(ChordTO::getInstrument, ChordTO::setInstrument);

		Button b;
		if (originalDTO != null)
			addComponent(b = new ModifyButton(event -> save(originalDTO, binder)));
		else
			addComponent(b = new CreateButton(event -> save(originalDTO, binder)));
		setComponentAlignment(b, Alignment.MIDDLE_CENTER);

		if (originalDTO != null) {
			binder.readBean(originalDTO);
			formTO.setConfiguration(originalDTO.getConfiguration());
		}
	}

	private void refreshDescriptionLayout(ChordTO originalDTO, ChordTO formTO, VerticalLayout chordDescriptionLayout,
			Instrument instrument) {
		chordDescriptionLayout.removeAllComponents();
		switch (instrument) {
		case GUITAR:
			refreshDescriptionLayoutAsGuitar(originalDTO, formTO, chordDescriptionLayout);
			break;
		default:
			break;
		}
	}

	private void refreshDescriptionLayoutAsGuitar(ChordTO originalDTO, ChordTO formTO,
			VerticalLayout chordDescriptionLayout) {
		ComboBox<Integer> fretCombo = new ComboBox<Integer>("Od pražce",
				IntStream.rangeClosed(1, 17).boxed().collect(Collectors.toList()));
		chordDescriptionLayout.addComponent(fretCombo);

		String[] stringsLabel = new String[] { "E", "a", "d", "g", "h", "e" };

		GridLayout layout = new GridLayout(6, 9);
		layout.setSpacing(false);
		layout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		chordDescriptionLayout.addComponent(layout);
		for (int row = 0; row < layout.getRows(); row++) {
			if (row % 2 != 0) {
				Label hrLabel = new Label("<hr/>", ContentMode.HTML);
				hrLabel.setWidth("100%");
				layout.addComponent(hrLabel, 0, row, layout.getColumns() - 1, row);
			} else
				for (int col = 0; col < layout.getColumns(); col++)
					if (row == 0) {
						String val = stringsLabel[col];
						layout.addComponent(new Label(val), col, row);
					} else {
						CheckBox cb = new CheckBox();
						layout.addComponent(cb, col, row);
						int bitPos = 1 << ((row / 2 - 1) * layout.getColumns() + col);
						if (originalDTO != null)
							cb.setValue((originalDTO.getConfiguration().intValue() & bitPos) > 0);
						cb.addValueChangeListener(val -> {
							if (val.getValue())
								formTO.setConfiguration(formTO.getConfiguration().intValue() | bitPos);
							else
								formTO.setConfiguration(formTO.getConfiguration().intValue() & ~bitPos);
						});
					}
		}
	}

	private void save(ChordTO originalTO, Binder<ChordTO> binder) {
		try {
			ChordTO writeTO = originalTO == null ? new ChordTO() : originalTO;
			binder.writeBean(writeTO);
			writeTO.setConfiguration(binder.getBean().getConfiguration());
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
