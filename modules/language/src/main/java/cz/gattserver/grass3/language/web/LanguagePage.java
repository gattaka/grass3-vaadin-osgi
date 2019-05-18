package cz.gattserver.grass3.language.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.gattserver.grass3.language.facades.LanguageFacade;
import cz.gattserver.grass3.language.model.domain.ItemType;
import cz.gattserver.grass3.language.model.dto.CrosswordCell;
import cz.gattserver.grass3.language.model.dto.CrosswordHintTO;
import cz.gattserver.grass3.language.model.dto.CrosswordTO;
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.language.model.dto.LanguageTO;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.GridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.ui.BoldLabel;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.WebWindow;

public class LanguagePage extends OneColumnPage {

	private static final String PREKLAD_LABEL = "Překlad";

	@Autowired
	private LanguageFacade languageFacade;

	@Autowired
	private SecurityService securityService;

	private TabSheet tabSheet;
	private VerticalLayout testLayout;

	public LanguagePage(GrassRequest request) {
		super(request);
	}

	private void createTabSheet(long langId) {
		tabSheet = new TabSheet();
		tabSheet.addSelectedTabChangeListener(e -> {
			if (!e.isUserOriginated())
				return;
			Component selectedTab = tabSheet.getSelectedTab();
			Tab tab = tabSheet.getTab(selectedTab);
			int pos = tabSheet.getTabPosition(tab);
			if (pos < 3) {
				Component newTab = null;
				switch (pos) {
				case 0:
					newTab = createItemsTab(langId, null);
					break;
				case 1:
					newTab = createItemsTab(langId, ItemType.WORD);
					break;
				case 2:
					newTab = createItemsTab(langId, ItemType.PHRASE);
					break;
				default:
					break;
				}
				tabSheet.replaceComponent(selectedTab, newTab);
			}
		});
	}

	@Override
	protected Component createContent() {

		Page.getCurrent().getStyles()
				.add("input.v-textfield.v-disabled.v-widget.crossword-cell.v-textfield-crossword-cell.v-has-width, "
						+ "input.v-textfield.v-widget.v-has-width.v-has-height.v-disabled.crossword-cell.v-textfield-crossword-cell.crossword-done.v-textfield-crossword-done, "
						+ "input.v-textfield.v-widget.crossword-cell.v-textfield-crossword-cell.v-has-width { "
						+ "text-align: center; " + "font-variant: small-caps;" + "padding: 0;" + "}");

		Page.getCurrent().getStyles()
				.add("input.v-textfield.v-widget.v-has-width.v-has-height.v-disabled.crossword-cell.v-textfield-crossword-cell.crossword-done.v-textfield-crossword-done { "
						+ "background-color: #cef29c; " + "}");

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		VerticalLayout langLayout = new VerticalLayout();
		layout.addComponent(langLayout);

		List<LanguageTO> langs = languageFacade.getLanguages();
		Grid<LanguageTO> grid = new Grid<>(null, langs);
		grid.setWidth("100%");
		grid.setHeight("150px");
		grid.addColumn(LanguageTO::getName).setCaption("Název");
		langLayout.addComponent(grid);

		HorizontalLayout btnLayout = new HorizontalLayout();
		if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN))
			langLayout.addComponent(btnLayout);

		grid.addSelectionListener(se -> se.getFirstSelectedItem().ifPresent(item -> {
			if (tabSheet != null)
				langLayout.removeComponent(tabSheet);

			long langId = item.getId();
			createTabSheet(langId);

			tabSheet.addTab(createItemsTab(langId, null), "Vše");
			tabSheet.addTab(new VerticalLayout(), "Slovíčka");
			tabSheet.addTab(new VerticalLayout(), "Fráze");
			if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN))
				tabSheet.addTab(createTestTab(langId), "Zkoušení");
			tabSheet.addTab(createCrosswordTab(langId), "Křížovka");

			langLayout.addComponent(tabSheet);
		}));

		btnLayout.addComponent(
				new CreateGridButton("Přidat", event -> UI.getCurrent().addWindow(new LanguageWindow(to -> {
					languageFacade.saveLanguage(to);
					langs.clear();
					langs.addAll(languageFacade.getLanguages());
					grid.getDataProvider().refreshAll();
				}))));

		btnLayout.addComponent(new ModifyGridButton<LanguageTO>("Upravit",
				item -> UI.getCurrent().addWindow(new LanguageWindow(item, to -> {
					languageFacade.saveLanguage(to);
					langs.clear();
					langs.addAll(languageFacade.getLanguages());
					grid.getDataProvider().refreshAll();
				})), grid));

		if (!langs.isEmpty())
			grid.select(langs.get(0));

		return layout;
	}

	private Component createCrosswordTab(long langId) {
		VerticalLayout sheet = new VerticalLayout();
		sheet.setMargin(new MarginInfo(true, false, false, false));

		HorizontalLayout btnLayout = new HorizontalLayout();
		sheet.addComponent(btnLayout);

		Map<TextField, String> fieldMap = new HashMap<>();

		Button giveUpTestBtn = new Button("Vzdát to", event -> {
			for (Map.Entry<TextField, String> entry : fieldMap.entrySet())
				entry.getKey().setValue(entry.getValue());
		});
		giveUpTestBtn.setIcon(ImageIcon.FLAG_16_ICON.createResource());
		btnLayout.addComponent(giveUpTestBtn);

		Slider slider = new Slider(5, 30);

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setMargin(new MarginInfo(true, false, false, false));
		mainLayout.setSpacing(true);

		Button newCrosswordBtn = new Button("",
				event -> generateNewCrossword(slider.getValue().intValue(), langId, fieldMap, mainLayout));
		newCrosswordBtn.setIcon(ImageIcon.RIGHT_16_ICON.createResource());
		btnLayout.addComponent(newCrosswordBtn);

		slider.addValueChangeListener(e -> newCrosswordBtn
				.setCaption("Nová křížovka " + e.getValue().intValue() + "x" + e.getValue().intValue()));

		slider.setValue(15.0);

		Button easierCrosswordBtn = new Button("",
				event -> slider.setValue(Math.max(slider.getMin(), slider.getValue() - 1)));
		easierCrosswordBtn.setIcon(ImageIcon.DOWN_16_ICON.createResource());
		btnLayout.addComponent(easierCrosswordBtn);

		btnLayout.addComponent(slider);

		Button harderCrosswordBtn = new Button("",
				event -> slider.setValue(Math.min(slider.getMax(), slider.getValue() + 1)));
		harderCrosswordBtn.setIcon(ImageIcon.UP_16_ICON.createResource());
		btnLayout.addComponent(harderCrosswordBtn);

		sheet.addComponent(mainLayout);

		return sheet;
	}

	private void generateNewCrossword(int size, long langId, Map<TextField, String> fieldMap,
			VerticalLayout mainLayout) {

		// clear
		fieldMap.clear();
		mainLayout.removeAllComponents();

		LanguageItemTO filterTO = new LanguageItemTO();
		filterTO.setLanguage(langId);
		filterTO.setType(ItemType.WORD);

		CrosswordTO crosswordTO = languageFacade.prepareCrossword(filterTO, size);

		if (crosswordTO.getHints().isEmpty()) {
			mainLayout.addComponent(new Label("Nezdařilo se sestavit křížovku"));
			return;
		}

		List<CrosswordField> writeFields = new ArrayList<>();

		GridLayout hintsLayout = new GridLayout(6, Math.max(1, crosswordTO.getHints().size() / 2));
		hintsLayout.setSpacing(true);
		hintsLayout.setWidth("100%");
		hintsLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
		for (CrosswordHintTO to : crosswordTO.getHints()) {
			hintsLayout.addComponent(new Label(to.getId() + "."));
			CrosswordField tf = new CrosswordField(to);
			writeFields.add(tf);
			tf.setMaxLength(to.getWordLength());
			hintsLayout.addComponent(tf);
			Label hintLabel = new Label(to.getHint());
			hintsLayout.addComponent(hintLabel);
			hintsLayout.setComponentAlignment(hintLabel, Alignment.MIDDLE_LEFT);
		}

		GridLayout crosswordLayout = constructCrossword(crosswordTO, writeFields, fieldMap);

		mainLayout.addComponent(crosswordLayout);
		mainLayout.setComponentAlignment(crosswordLayout, Alignment.MIDDLE_CENTER);
		mainLayout.addComponent(hintsLayout);
	}

	private GridLayout constructCrossword(CrosswordTO crosswordTO, List<CrosswordField> writeFields,
			Map<TextField, String> fieldMap) {
		GridLayout crosswordLayout = new GridLayout(crosswordTO.getWidth(), crosswordTO.getHeight());
		crosswordLayout.setMargin(new MarginInfo(false, false, true, false));
		crosswordLayout.setSpacing(false);

		for (int y = 0; y < crosswordTO.getHeight(); y++) {
			for (int x = 0; x < crosswordTO.getWidth(); x++) {
				CrosswordCell cell = crosswordTO.getCell(x, y);
				if (cell != null) {
					TextField t = new TextField();
					t.addStyleName("crossword-cell");
					t.setWidth("25px");
					t.setHeight("25px");
					t.setEnabled(cell.isWriteAllowed());
					if (!cell.isWriteAllowed()) {
						t.setValue(cell.getValue());
					} else {
						t.setMaxLength(1);
						connectField(t, cell, x, y, writeFields, fieldMap);
					}
					crosswordLayout.addComponent(t, x, y);
				}
			}
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
			tf.addStyleName("crossword-done");
			tf.setEnabled(false);
		}
	}

	private VerticalLayout createTestTab(Long langId) {
		VerticalLayout sheet = new VerticalLayout();
		sheet.setMargin(new MarginInfo(true, false, false, false));

		HorizontalLayout wordTestLayout = new HorizontalLayout();
		sheet.addComponent(wordTestLayout);

		Button wordsTestBtn = new Button("Spustit test slovíček",
				event -> startTest(langId, ItemType.WORD, testLayout));
		wordsTestBtn.setIcon(ImageIcon.RIGHT_16_ICON.createResource());
		wordTestLayout.addComponent(wordsTestBtn);

		Float wordsProgress = languageFacade.getSuccessRateOfLanguageAndType(ItemType.WORD, langId);
		ProgressBar wordsSuccessBar = new ProgressBar(wordsProgress);
		wordTestLayout.addComponent(wordsSuccessBar);
		wordTestLayout.setComponentAlignment(wordsSuccessBar, Alignment.MIDDLE_LEFT);
		Label wordsProgressLabel = new Label((int) (wordsProgress * 100) + "%");
		wordTestLayout.addComponent(wordsProgressLabel);
		wordTestLayout.setComponentAlignment(wordsProgressLabel, Alignment.MIDDLE_LEFT);
		wordsSuccessBar.setWidth("200px");

		HorizontalLayout phrasesTestLayout = new HorizontalLayout();
		sheet.addComponent(phrasesTestLayout);

		Button phrasesTestBtn = new Button("Spustit test frází",
				event -> startTest(langId, ItemType.PHRASE, testLayout));
		phrasesTestBtn.setIcon(ImageIcon.RIGHT_16_ICON.createResource());
		phrasesTestLayout.addComponent(phrasesTestBtn);

		Float phrasesProgress = languageFacade.getSuccessRateOfLanguageAndType(ItemType.PHRASE, langId);
		ProgressBar phrasesSuccessBar = new ProgressBar(phrasesProgress);
		phrasesTestLayout.addComponent(phrasesSuccessBar);
		phrasesTestLayout.setComponentAlignment(phrasesSuccessBar, Alignment.MIDDLE_LEFT);
		Label phrasesProgressLabel = new Label((int) (phrasesProgress * 100) + "%");
		phrasesTestLayout.addComponent(phrasesProgressLabel);
		phrasesTestLayout.setComponentAlignment(phrasesProgressLabel, Alignment.MIDDLE_LEFT);
		phrasesSuccessBar.setWidth("200px");

		Button allTestBtn = new Button("Spustit test všeho", event -> startTest(langId, null, testLayout));
		allTestBtn.setIcon(ImageIcon.RIGHT_16_ICON.createResource());
		sheet.addComponent(allTestBtn);

		testLayout = new VerticalLayout();
		testLayout.setMargin(false);
		testLayout.addComponent(new Label("Vyberte test"));
		sheet.addComponent(testLayout);

		return sheet;
	}

	private void createGridLine(LanguageItemTO item, GridLayout gridLayout, Map<LanguageItemTO, TextField> answersMap) {
		Label label = new Label(item.getTranslation());
		label.setWidth(null);
		gridLayout.addComponent(label);

		TextField answerField = new TextField();
		answerField.setWidth("100%");
		answerField.setPlaceholder("varianta;varianta;...");
		gridLayout.addComponent(answerField);

		answersMap.put(item, answerField);
	}

	private void startTest(Long langId, ItemType type, VerticalLayout testLayout) {
		testLayout.removeAllComponents();

		Map<LanguageItemTO, TextField> answersMap = new LinkedHashMap<>();

		List<LanguageItemTO> itemsToLearn = languageFacade.getLanguageItemsForTest(langId, 0, 0.1, 10, type);
		List<LanguageItemTO> itemsToImprove = languageFacade.getLanguageItemsForTest(langId, 0.1, 0.8, 5, type);
		List<LanguageItemTO> itemsToRefresh = languageFacade.getLanguageItemsForTest(langId, 0.8, 1.1, 4, type);

		int linesCount = 1 + itemsToLearn.size() + itemsToImprove.size() + itemsToRefresh.size() + 3;

		GridLayout gridLayout = new GridLayout(2, linesCount);
		gridLayout.setSpacing(true);
		gridLayout.setMargin(new MarginInfo(true, false, false, false));
		gridLayout.setWidth("100%");
		gridLayout.setColumnExpandRatio(1, 1);
		testLayout.addComponent(gridLayout);

		gridLayout.addComponent(new BoldLabel("Položka"));
		gridLayout.addComponent(new BoldLabel(PREKLAD_LABEL));

		int line = 1;

		gridLayout.addComponent(new BoldLabel("Nové"), 0, line, 1, line);
		for (LanguageItemTO item : itemsToLearn)
			createGridLine(item, gridLayout, answersMap);

		line += itemsToLearn.size() + 1;

		gridLayout.addComponent(new BoldLabel("Ke zlepšení"), 0, line, 1, line);
		for (LanguageItemTO item : itemsToImprove)
			createGridLine(item, gridLayout, answersMap);

		line += itemsToImprove.size() + 1;

		gridLayout.addComponent(new BoldLabel("Opakování"), 0, line, 1, line);
		for (LanguageItemTO item : itemsToRefresh)
			createGridLine(item, gridLayout, answersMap);

		Button submitBtn = new Button("Zkontrolovat");
		submitBtn.addClickListener(e -> {
			testLayout.removeAllComponents();

			GridLayout resultLayout = new GridLayout(4, linesCount - 3);
			resultLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
			resultLayout.setMargin(new MarginInfo(true, false, false, false));
			resultLayout.setSpacing(true);
			resultLayout.setWidth("100%");
			resultLayout.setColumnExpandRatio(3, 1);
			testLayout.addComponent(resultLayout);

			resultLayout.addComponent(new BoldLabel("Položka"), 0, 0, 1, 0);
			resultLayout.addComponent(new BoldLabel(PREKLAD_LABEL));
			resultLayout.addComponent(new BoldLabel("Odpověď"));

			answersMap.keySet().forEach(item -> {
				TextField answerField = answersMap.get(item);
				String answer = answerField.getValue();
				String correctAnswer = item.getContent().toLowerCase().trim();

				boolean success = false;
				for (String variant : answer.toLowerCase().split(";")) {
					if (variant.trim().equals(correctAnswer)) {
						success = true;
						break;
					}
				}
				Embedded image = new Embedded(null,
						(success ? ImageIcon.TICK_16_ICON : ImageIcon.DELETE_16_ICON).createResource());
				resultLayout.addComponent(image);
				resultLayout.setComponentAlignment(image, Alignment.BOTTOM_LEFT);

				Label label = new Label(item.getTranslation());
				label.setWidth(null);
				resultLayout.addComponent(label);

				Label resultCorrect = new BoldLabel(item.getContent());
				resultLayout.addComponent(resultCorrect);

				TextField resultAnswerField = new TextField(null, answer);
				resultAnswerField.setEnabled(false);
				resultAnswerField.setWidth("100%");
				resultLayout.addComponent(resultAnswerField);

				languageFacade.updateItemAfterTest(item, success);

			});
		});
		testLayout.addComponent(submitBtn);

	}

	private void populate(Grid<LanguageItemTO> grid, LanguageItemTO filterTO) {
		grid.setDataProvider((sortOrder, offset, limit) -> languageFacade
				.getLanguageItems(filterTO, offset, limit, sortOrder).stream(),
				() -> languageFacade.countLanguageItems(filterTO));
	}

	private VerticalLayout createItemsTab(Long langId, ItemType type) {
		VerticalLayout sheet = new VerticalLayout();
		sheet.setMargin(new MarginInfo(true, false, false, false));

		LanguageItemTO filterTO = new LanguageItemTO();
		filterTO.setLanguage(langId);
		filterTO.setType(type);

		Grid<LanguageItemTO> grid = new Grid<>();
		grid.setWidth("100%");
		grid.setHeight("500px");

		Column<LanguageItemTO, String> contentColumn = grid.addColumn(LanguageItemTO::getContent).setCaption("Obsah")
				.setSortProperty("content");
		Column<LanguageItemTO, String> translationColumn = grid.addColumn(LanguageItemTO::getTranslation)
				.setCaption(PREKLAD_LABEL).setSortProperty("translation");

		if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN)) {
			grid.addColumn(item -> (Math.floor(item.getSuccessRate() * 1000) / 10) + "%").setCaption("Úspěšnost")
					.setStyleGenerator(item -> "v-align-right").setSortProperty("successRate");
			grid.addColumn(LanguageItemTO::getLastTested, new LocalDateTimeRenderer("dd.MM.yyyy HH:mm"))
					.setCaption("Naposledy zkoušeno").setStyleGenerator(item -> "v-align-right")
					.setSortProperty("lastTested");
			grid.addColumn(LanguageItemTO::getTested).setCaption("Zkoušeno").setSortProperty("tested");
			grid.addColumn(LanguageItemTO::getId).setCaption("Id").setSortProperty("id");
		}

		grid.sort(contentColumn);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Obsah
		TextField contentFilterField = new TextField();
		contentFilterField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		contentFilterField.setWidth("100%");
		contentFilterField.addValueChangeListener(e -> {
			filterTO.setContent(e.getValue());
			populate(grid, filterTO);
		});
		filteringHeader.getCell(contentColumn).setComponent(contentFilterField);

		// Překlad
		TextField translationFilterField = new TextField();
		translationFilterField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		translationFilterField.setWidth("100%");
		translationFilterField.addValueChangeListener(e -> {
			filterTO.setTranslation(e.getValue());
			populate(grid, filterTO);
		});
		filteringHeader.getCell(translationColumn).setComponent(translationFilterField);

		populate(grid, filterTO);

		sheet.addComponent(grid);

		if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN))
			sheet.addComponent(createButtonLayout(grid, langId, type));

		return sheet;
	}

	private HorizontalLayout createButtonLayout(Grid<LanguageItemTO> grid, long langId, ItemType type) {
		HorizontalLayout btnLayout = new HorizontalLayout();

		btnLayout.addComponent(
				new CreateGridButton("Přidat", event -> UI.getCurrent().addWindow(new LanguageItemWindow(to -> {
					to.setLanguage(langId);
					languageFacade.saveLanguageItem(to);
					grid.getDataProvider().refreshAll();
				}, langId, type))));

		btnLayout.addComponent(new ModifyGridButton<LanguageItemTO>("Upravit", item -> {
			ItemType oldType = item.getType();
			UI.getCurrent().addWindow(new LanguageItemWindow(item, to -> {
				languageFacade.saveLanguageItem(to);
				if (oldType.equals(to.getType()))
					grid.getDataProvider().refreshItem(to);
				else
					grid.getDataProvider().refreshAll();
			}, langId, type));
		}, grid));

		btnLayout.addComponent(new DeleteGridButton<LanguageItemTO>("Odstranit", items -> items.forEach(item -> {
			languageFacade.deleteLanguageItem(item);
			grid.getDataProvider().refreshAll();
		}), grid));

		GridButton<LanguageItemTO> moveBtn = new GridButton<>("Změnit jazyk", items -> changeLangOfItems(items, grid),
				grid);
		moveBtn.setIcon(ImageIcon.MOVE_16_ICON.createResource());
		btnLayout.addComponent(moveBtn);

		String caption;
		if (ItemType.WORD == type)
			caption = "slovíček";
		else if (ItemType.PHRASE == type)
			caption = "frází";
		else
			caption = "všeho";

		Button testBtn = new Button("Spustit test " + caption, event -> {
			tabSheet.setSelectedTab(3);
			startTest(langId, type, testLayout);
		});
		testBtn.setIcon(ImageIcon.RIGHT_16_ICON.createResource());
		btnLayout.addComponent(testBtn);

		if (type != null) {
			Float wordsProgress = languageFacade.getSuccessRateOfLanguageAndType(type, langId);
			ProgressBar wordsSuccessBar = new ProgressBar(wordsProgress);
			btnLayout.addComponent(wordsSuccessBar);
			btnLayout.setComponentAlignment(wordsSuccessBar, Alignment.MIDDLE_LEFT);
			Label wordsProgressLabel = new Label((int) (wordsProgress * 100) + "%");
			btnLayout.addComponent(wordsProgressLabel);
			btnLayout.setComponentAlignment(wordsProgressLabel, Alignment.MIDDLE_LEFT);
			wordsSuccessBar.setWidth("200px");
		}

		return btnLayout;
	}

	private void changeLangOfItems(Set<LanguageItemTO> items, Grid<LanguageItemTO> grid) {
		Window w = new WebWindow("Změna jazyka");

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		w.setContent(layout);

		List<LanguageTO> langs = languageFacade.getLanguages();
		Grid<LanguageTO> targatGrid = new Grid<>(null, langs);
		targatGrid.addColumn(LanguageTO::getName).setCaption("Název");
		layout.addComponent(targatGrid);

		targatGrid.addSelectionListener(se -> se.getFirstSelectedItem().ifPresent(lang -> items.forEach(item -> {
			languageFacade.moveLanguageItemTo(item, lang);
			targatGrid.getDataProvider().refreshAll();
			w.close();
			grid.getDataProvider().refreshAll();
		})));

		UI.getCurrent().addWindow(w);
	}

}
