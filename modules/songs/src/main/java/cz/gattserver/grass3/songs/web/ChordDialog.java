package cz.gattserver.grass3.songs.web;

import java.util.Arrays;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.songs.model.domain.Instrument;
import cz.gattserver.grass3.songs.model.interfaces.ChordTO;
import cz.gattserver.grass3.ui.components.SaveCloseButtons;
import cz.gattserver.grass3.ui.util.UIUtils;
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
		setWidth("300px");

		ChordTO formTO = new ChordTO();
		formTO.setInstrument(Instrument.GUITAR);

		Binder<ChordTO> binder = new Binder<>(ChordTO.class);
		binder.setBean(formTO);

		final TextField nameField = new TextField("Název");
		nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		binder.forField(nameField).asRequired().bind(ChordTO::getName, ChordTO::setName);
		nameField.setWidth("100%");
		add(nameField);

		final ComboBox<Instrument> instrumentField = new ComboBox<>("Nástroj", Arrays.asList(Instrument.values()));
		instrumentField.setItemLabelGenerator(Instrument::getCaption);
		instrumentField.setWidth("100%");
		add(instrumentField);

		VerticalLayout chordDescriptionLayout = new VerticalLayout();
		chordDescriptionLayout.setMargin(false);
		add(chordDescriptionLayout);
		instrumentField.addValueChangeListener(
				e -> refreshDescriptionLayout(binder, originalDTO, formTO, chordDescriptionLayout, e.getValue()));
		add(chordDescriptionLayout);

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
		layout.setWidthFull();
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
						Div stringLabel = new Div();
						stringLabel.setText(val);
						stringLabel.getStyle().set("padding", "0 7px 0 6px").set("display", "inline-block");
						layout.add(stringLabel);
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
				}
				layout.add(new Breakline());
			}
		}
	}

	private void save(Binder<ChordTO> binder) {
		ChordTO writeTO = new ChordTO();
		if (binder.writeBeanIfValid(writeTO)) {
			try {
				writeTO.setConfiguration(binder.getBean().getConfiguration());
				writeTO.setId(binder.getBean().getId());
				onSave(writeTO);
				close();
			} catch (Exception ve) {
				new ErrorDialog("Uložení se nezdařilo").open();
			}
		}
	}

	protected abstract void onSave(ChordTO to);

}
