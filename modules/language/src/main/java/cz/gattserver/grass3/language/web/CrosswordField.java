package cz.gattserver.grass3.language.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import cz.gattserver.grass3.language.model.dto.CrosswordHintTO;

public class CrosswordField extends TextField {

	private static final long serialVersionUID = -6813919720512048177L;

	private CrosswordHintTO hintTO;
	private List<TextField> cellsFields;

	public CrosswordField(CrosswordHintTO hintTO) {
		this.hintTO = hintTO;
		cellsFields = new ArrayList<>(Arrays.asList(new TextField[hintTO.getWordLength()]));
		setValueChangeMode(ValueChangeMode.EAGER);
		addValueChangeListener(e -> {
			String value = e.getValue();
			for (int i = 0; i < cellsFields.size(); i++) {
				TextField tf = cellsFields.get(i);
				if (value.length() > i)
					tf.setValue(String.valueOf(value.charAt(i)));
				else
					tf.setValue("");
			}
		});
	}

	public CrosswordHintTO getHintTO() {
		return hintTO;
	}

	public void tryRegisterCellField(TextField cellField, int x, int y) {
		if (hintTO.isHorizontally() && y == hintTO.getFromY() && x >= hintTO.getFromX() && x <= hintTO.getToX())
			cellsFields.set(x - hintTO.getFromX(), cellField);

		if (!hintTO.isHorizontally() && x == hintTO.getFromX() && y >= hintTO.getFromY() && y <= hintTO.getToY())
			cellsFields.set(y - hintTO.getFromY(), cellField);
	}

}
