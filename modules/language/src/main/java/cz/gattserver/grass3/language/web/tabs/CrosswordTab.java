package cz.gattserver.grass3.language.web.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import cz.gattserver.grass3.language.facades.LanguageFacade;
import cz.gattserver.grass3.language.model.domain.ItemType;
import cz.gattserver.grass3.language.model.dto.CrosswordCell;
import cz.gattserver.grass3.language.model.dto.CrosswordHintTO;
import cz.gattserver.grass3.language.model.dto.CrosswordTO;
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.language.web.CrosswordField;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.Breakline;
import cz.gattserver.web.common.ui.ImageIcon;

public class CrosswordTab extends Div {

	private static final long serialVersionUID = 6332893829812704996L;

	@Autowired
	private LanguageFacade languageFacade;

	public CrosswordTab(Long langId) {
		SpringContextHelper.inject(this);

		ButtonLayout btnLayout = new ButtonLayout();
		add(btnLayout);

		Map<TextField, String> fieldMap = new HashMap<>();

		Button giveUpTestBtn = new Button("Vzdát to", event -> {
			for (Map.Entry<TextField, String> entry : fieldMap.entrySet())
				entry.getKey().setValue(entry.getValue());
		});
		giveUpTestBtn.setIcon(new Image(ImageIcon.FLAG_16_ICON.createResource(), "giveup"));
		btnLayout.add(giveUpTestBtn);

		NumberField numberField = new NumberField();
		numberField.setHasControls(true);
		numberField.setMin(5);
		numberField.setMax(30);
		add(numberField);

		VerticalLayout mainLayout = new VerticalLayout();

		Button newCrosswordBtn = new Button("",
				event -> generateNewCrossword(numberField.getValue().intValue(), langId, fieldMap, mainLayout));
		newCrosswordBtn.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "start"));
		btnLayout.add(newCrosswordBtn);

		numberField.addValueChangeListener(e -> newCrosswordBtn
				.setText("Nová křížovka " + e.getValue().intValue() + "x" + e.getValue().intValue()));

		numberField.setValue(15.0);
	}

	private void generateNewCrossword(int size, long langId, Map<TextField, String> fieldMap,
			VerticalLayout mainLayout) {

		// clear
		fieldMap.clear();
		mainLayout.removeAll();

		LanguageItemTO filterTO = new LanguageItemTO();
		filterTO.setLanguage(langId);
		filterTO.setType(ItemType.WORD);

		CrosswordTO crosswordTO = languageFacade.prepareCrossword(filterTO, size);

		if (crosswordTO.getHints().isEmpty()) {
			mainLayout.add("Nezdařilo se sestavit křížovku");
			return;
		}

		List<CrosswordField> writeFields = new ArrayList<>();

		Div hintsLayout = new Div();
		hintsLayout.setWidth("100%");
		for (CrosswordHintTO to : crosswordTO.getHints()) {
			hintsLayout.add(to.getId() + ".");
			CrosswordField tf = new CrosswordField(to);
			writeFields.add(tf);
			tf.setMaxLength(to.getWordLength());
			hintsLayout.add(tf);
			Span hintLabel = new Span(to.getHint());
			hintsLayout.add(hintLabel);
		}

		Div crosswordLayout = constructCrossword(crosswordTO, writeFields, fieldMap);

		mainLayout.add(crosswordLayout);
		mainLayout.add(hintsLayout);
	}

	private Div constructCrossword(CrosswordTO crosswordTO, List<CrosswordField> writeFields,
			Map<TextField, String> fieldMap) {
		Div crosswordLayout = new Div();

		for (int y = 0; y < crosswordTO.getHeight(); y++) {
			for (int x = 0; x < crosswordTO.getWidth(); x++) {
				CrosswordCell cell = crosswordTO.getCell(x, y);
				if (cell != null) {
					TextField t = new TextField();
					t.addClassName("crossword-cell");
					t.setWidth("25px");
					t.setHeight("25px");
					t.setEnabled(cell.isWriteAllowed());
					if (!cell.isWriteAllowed()) {
						t.setValue(cell.getValue());
					} else {
						t.setMaxLength(1);
						connectField(t, cell, x, y, writeFields, fieldMap);
					}
					crosswordLayout.add(t);
				} else {
					Div spacer = new Div();
					spacer.setWidth("25px");
					spacer.setHeight("25px");
					crosswordLayout.add(spacer);
				}
			}
			crosswordLayout.add(new Breakline());
		}
		return crosswordLayout;
	}

	private void connectField(TextField t, CrosswordCell cell, int x, int y, List<CrosswordField> writeFields,
			Map<TextField, String> fieldMap) {
		// logika pro zapipsování skrz postranní pole
		for (CrosswordField cf : writeFields)
			cf.tryRegisterCellField(t, x, y);

		// logika pro kontrolu správného výsledku
		fieldMap.put(t, cell.getValue());
		t.addValueChangeListener(e -> checkCrossword(fieldMap));
	}

	private void checkCrossword(Map<TextField, String> fieldMap) {
		for (Map.Entry<TextField, String> entry : fieldMap.entrySet()) {
			String is = entry.getKey().getValue();
			String shouldBe = entry.getValue();
			if (StringUtils.isNotBlank(shouldBe) && !shouldBe.equalsIgnoreCase(is)
					|| StringUtils.isBlank(shouldBe) && StringUtils.isNotBlank(is))
				return;
		}
		for (TextField tf : fieldMap.keySet()) {
			tf.addClassName("crossword-done");
			tf.setEnabled(false);
		}
	}

}
