package org.myftp.gattserver.grass3.tone;

import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.data.Property;

public class ToneWindow extends OneColumnWindow {

	private static final long serialVersionUID = -950042653154868289L;

	public static final String NAME = "tone";
	private Label resultChordTones;

	@Override
	protected void createContent(VerticalLayout layout) {

		layout.setSpacing(true);
		layout.setMargin(true);

		final ComboBox toneCombo = new ComboBox("Počáteční tón");
		for (Tone tone : Tone.tones) {
			toneCombo.addItem(tone);
		}
		layout.addComponent(toneCombo);

		final ComboBox chordTypeCombo = new ComboBox("Typ akordu");
		for (ChordType chordType : ChordType.values()) {
			chordTypeCombo.addItem(chordType);
		}
		layout.addComponent(chordTypeCombo);

		toneCombo.setFilteringMode(Filtering.FILTERINGMODE_OFF);
		toneCombo.setImmediate(true);
		toneCombo.addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = -1877029134512621156L;

			public void valueChange(ValueChangeEvent event) {
				calculateChord((Tone) toneCombo.getValue(),
						(ChordType) chordTypeCombo.getValue());
			}
		});

		chordTypeCombo.setFilteringMode(Filtering.FILTERINGMODE_OFF);
		chordTypeCombo.setImmediate(true);
		chordTypeCombo.addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = -1877029134512621156L;

			public void valueChange(ValueChangeEvent event) {
				calculateChord((Tone) toneCombo.getValue(),
						(ChordType) chordTypeCombo.getValue());
			}
		});

		resultChordTones = new Label();
		layout.addComponent(resultChordTones);

	}

	private void calculateChord(Tone tone, ChordType chordType) {

		ToneCalculator calculator = new ToneCalculator();

		if (tone == null || chordType == null) {
			resultChordTones.setValue("Zvolte počáteční tón a typ akordu");
		} else {
			resultChordTones.setValue(calculator.createChord(tone, chordType)
					.toString());
		}

	}

}
