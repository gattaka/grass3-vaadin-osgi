package cz.gattserver.grass3.language.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass3.language.facades.LanguageFacade;
import cz.gattserver.grass3.language.model.domain.ItemType;
import cz.gattserver.grass3.language.model.dto.CrosswordCell;
import cz.gattserver.grass3.language.model.dto.CrosswordHintTO;
import cz.gattserver.grass3.language.model.dto.CrosswordTO;
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.language.model.dto.LanguageTO;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.GridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.web.common.ui.Breakline;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.Strong;
import cz.gattserver.web.common.ui.window.WebDialog;

@Route("language")
public class LanguagePage extends OneColumnPage {

	private static final long serialVersionUID = 4767207674013382065L;

	private static final Logger logger = LoggerFactory.getLogger(LanguagePage.class);
	private static final String PREKLAD_LABEL = "Překlad";

	@Autowired
	private LanguageFacade languageFacade;

	@Autowired
	private SecurityService securityService;

	private Tabs tabs;
	private Div pageLayout;
	private VerticalLayout testLayout;

	public LanguagePage() {
		init();
	}

	private void createTabSheet(long langId) {
		tabs = new Tabs();
		tabs.addSelectedChangeListener(e -> {
			pageLayout.removeAll();
			switch (tabs.getSelectedIndex()) {
			default:
			case 0:
				createItemsTab(langId, null);
				break;
			case 1:
				createItemsTab(langId, ItemType.WORD);
				break;
			case 2:
				createItemsTab(langId, ItemType.PHRASE);
				break;
			case 3:
				break;
			case 4:
				createCrosswordTab(langId);
				break;
			case 5:
				createStatisticsTab(langId);
				break;
			}
		});
	}

	@Override
	protected void createColumnContent(Div layout) {

		// Page.getCurrent().getStyles()
		// .add("input.v-textfield.v-disabled.v-widget.crossword-cell.v-textfield-crossword-cell.v-has-width,
		// "
		// +
		// "input.v-textfield.v-widget.v-has-width.v-has-height.v-disabled.crossword-cell.v-textfield-crossword-cell.crossword-done.v-textfield-crossword-done,
		// "
		// +
		// "input.v-textfield.v-widget.crossword-cell.v-textfield-crossword-cell.v-has-width
		// { "
		// + "text-align: center; " + "font-variant: small-caps;" + "padding:
		// 0;" + "}");
		//
		// Page.getCurrent().getStyles()
		// .add("input.v-textfield.v-widget.v-has-width.v-has-height.v-disabled.crossword-cell.v-textfield-crossword-cell.crossword-done.v-textfield-crossword-done
		// { "
		// + "background-color: #cef29c; " + "}");

		List<LanguageTO> langs = languageFacade.getLanguages();
		Grid<LanguageTO> grid = new Grid<>(LanguageTO.class);
		grid.setItems(langs);
		grid.setWidth("100%");
		grid.setHeight("150px");
		grid.addColumn(LanguageTO::getName).setHeader("Název");
		layout.add(grid);

		ButtonLayout btnLayout = new ButtonLayout();
		if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN))
			layout.add(btnLayout);

		Div langLayout = new Div();
		layout.add(langLayout);

		grid.addSelectionListener(se -> se.getFirstSelectedItem().ifPresent(item -> {
			langLayout.removeAll();

			long langId = item.getId();
			createTabSheet(langId);

			tabs.add(new Tab("Vše"));
			tabs.add(new Tab("Slovíčka"));
			tabs.add(new Tab("Fráze"));
			if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN))
				tabs.add(new Tab("Zkoušení"));
			tabs.add(new Tab("Křížovka"));
			if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN))
				tabs.add(new Tab("Statistiky"));

			langLayout.add(tabs);
		}));

		btnLayout.add(new CreateGridButton("Přidat", event -> new LanguageWindow(to -> {
			languageFacade.saveLanguage(to);
			langs.clear();
			langs.addAll(languageFacade.getLanguages());
			grid.getDataProvider().refreshAll();
		}).open()));

		btnLayout.add(new ModifyGridButton<LanguageTO>("Upravit", item -> new LanguageWindow(item, to -> {
			languageFacade.saveLanguage(to);
			langs.clear();
			langs.addAll(languageFacade.getLanguages());
			grid.getDataProvider().refreshAll();
		}).open(), grid));

		if (!langs.isEmpty())
			grid.select(langs.get(0));
	}

	private void createCrosswordTab(long langId) {
		ButtonLayout btnLayout = new ButtonLayout();
		pageLayout.add(btnLayout);

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
		pageLayout.add(numberField);

		VerticalLayout mainLayout = new VerticalLayout();

		Button newCrosswordBtn = new Button("",
				event -> generateNewCrossword(numberField.getValue().intValue(), langId, fieldMap, mainLayout));
		newCrosswordBtn.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "start"));
		btnLayout.add(newCrosswordBtn);

		numberField.addValueChangeListener(e -> newCrosswordBtn
				.setText("Nová křížovka " + e.getValue().intValue() + "x" + e.getValue().intValue()));

		numberField.setValue(15.0);

		pageLayout.add(mainLayout);
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

	private VerticalLayout createTestTab(Long langId) {
		VerticalLayout sheet = new VerticalLayout();

		HorizontalLayout wordTestLayout = new HorizontalLayout();
		sheet.add(wordTestLayout);

		Button wordsTestBtn = new Button("Spustit test slovíček", event -> startTest(langId, ItemType.WORD));
		wordsTestBtn.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "run"));
		wordTestLayout.add(wordsTestBtn);

		Float wordsProgress = languageFacade.getSuccessRateOfLanguageAndType(ItemType.WORD, langId);
		ProgressBar wordsSuccessBar = new ProgressBar(0, 100);
		wordsSuccessBar.setValue(wordsProgress);

		wordTestLayout.add(wordsSuccessBar);
		Span wordsProgressLabel = new Span((int) (wordsProgress * 100) + "%");
		wordTestLayout.add(wordsProgressLabel);
		wordsSuccessBar.setWidth("200px");

		HorizontalLayout phrasesTestLayout = new HorizontalLayout();
		sheet.add(phrasesTestLayout);

		Button phrasesTestBtn = new Button("Spustit test frází", event -> startTest(langId, ItemType.PHRASE));
		phrasesTestBtn.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "run"));
		phrasesTestLayout.add(phrasesTestBtn);

		Float phrasesProgress = languageFacade.getSuccessRateOfLanguageAndType(ItemType.PHRASE, langId);
		ProgressBar phrasesSuccessBar = new ProgressBar(0, 100);
		phrasesSuccessBar.setValue(phrasesProgress);
		phrasesTestLayout.add(phrasesSuccessBar);

		Span phrasesProgressLabel = new Span((int) (phrasesProgress * 100) + "%");
		phrasesTestLayout.add(phrasesProgressLabel);
		phrasesSuccessBar.setWidth("200px");

		Button allTestBtn = new Button("Spustit test všeho", event -> startTest(langId, null));
		allTestBtn.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "run"));
		sheet.add(allTestBtn);

		testLayout = new VerticalLayout();
		testLayout.setMargin(false);
		testLayout.add(new Span("Vyberte test"));
		sheet.add(testLayout);

		return sheet;
	}

	private void createGridLine(LanguageItemTO item, Div gridLayout, Map<LanguageItemTO, TextField> answersMap) {
		Div label = new Div();
		label.add(item.getTranslation());
		label.setWidth(null);
		gridLayout.add(label);

		TextField answerField = new TextField();
		answerField.setWidth("100%");
		answerField.setPlaceholder("varianta;varianta;...");
		gridLayout.add(answerField);

		answersMap.put(item, answerField);
	}

	private void startTest(Long langId, ItemType type) {
		testLayout.removeAll();

		Map<LanguageItemTO, TextField> answersMap = new LinkedHashMap<>();

		List<LanguageItemTO> itemsToLearn = languageFacade.getLanguageItemsForTest(langId, 0, 0.1, 10, type);
		List<LanguageItemTO> itemsToImprove = languageFacade.getLanguageItemsForTest(langId, 0.1, 0.8, 5, type);
		List<LanguageItemTO> itemsToRefresh = languageFacade.getLanguageItemsForTest(langId, 0.8, 1.1, 4, type);

		int linesCount = 1 + itemsToLearn.size() + itemsToImprove.size() + itemsToRefresh.size() + 3;

		Div gridLayout = new Div();
		gridLayout.setWidth("100%");
		testLayout.add(gridLayout);

		gridLayout.add(new Strong("Položka"));
		gridLayout.add(new Strong(PREKLAD_LABEL));

		int line = 1;

		gridLayout.add(new Strong("Nové"));
		for (LanguageItemTO item : itemsToLearn)
			createGridLine(item, gridLayout, answersMap);

		line += itemsToLearn.size() + 1;

		gridLayout.add(new Strong("Ke zlepšení"));
		for (LanguageItemTO item : itemsToImprove)
			createGridLine(item, gridLayout, answersMap);

		line += itemsToImprove.size() + 1;

		gridLayout.add(new Strong("Opakování"));
		for (LanguageItemTO item : itemsToRefresh)
			createGridLine(item, gridLayout, answersMap);

		Button submitBtn = new Button("Zkontrolovat");
		submitBtn.addClickListener(e -> {
			testLayout.removeAll();

			Div resultLayout = new Div();
			resultLayout.setWidth("100%");
			testLayout.add(resultLayout);

			resultLayout.add(new Strong("Položka"));
			resultLayout.add(new Strong(PREKLAD_LABEL));
			resultLayout.add(new Strong("Odpověď"));

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
				Image image = new Image((success ? ImageIcon.TICK_16_ICON : ImageIcon.DELETE_16_ICON).createResource(),
						"result");
				resultLayout.add(image);

				Div label = new Div();
				label.add(item.getTranslation());
				label.setWidth(null);
				resultLayout.add(label);

				resultLayout.add(new Strong(item.getContent()));

				TextField resultAnswerField = new TextField(null, answer);
				resultAnswerField.setEnabled(false);
				resultAnswerField.setWidth("100%");
				resultLayout.add(resultAnswerField);

				languageFacade.updateItemAfterTest(item, success);

			});
		});
		testLayout.add(submitBtn);
	}

	private void populate(Grid<LanguageItemTO> grid, LanguageItemTO filterTO) {
		FetchCallback<LanguageItemTO, LanguageItemTO> fetchCallback = q -> languageFacade
				.getLanguageItems(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
		CountCallback<LanguageItemTO, LanguageItemTO> countCallback = q -> languageFacade.countLanguageItems(filterTO);
		grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
	}

	private void createItemsTab(Long langId, ItemType type) {
		LanguageItemTO filterTO = new LanguageItemTO();
		filterTO.setLanguage(langId);
		filterTO.setType(type);

		Grid<LanguageItemTO> grid = new Grid<>();
		grid.setWidth("100%");
		grid.setHeight("500px");

		Column<LanguageItemTO> contentColumn = grid.addColumn(LanguageItemTO::getContent).setHeader("Obsah")
				.setSortProperty("content");
		Column<LanguageItemTO> translationColumn = grid.addColumn(LanguageItemTO::getTranslation)
				.setHeader(PREKLAD_LABEL).setSortProperty("translation");

		if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN)) {
			grid.addColumn(item -> (Math.floor(item.getSuccessRate() * 1000) / 10) + "%").setHeader("Úspěšnost")
					.setTextAlign(ColumnTextAlign.END).setSortProperty("successRate");
			grid.addColumn(new LocalDateTimeRenderer<LanguageItemTO>(LanguageItemTO::getLastTested, "dd.MM.yyyy HH:mm"))
					.setHeader("Naposledy zkoušeno").setTextAlign(ColumnTextAlign.END).setSortProperty("lastTested");
			grid.addColumn(LanguageItemTO::getTested).setHeader("Zkoušeno").setSortProperty("tested");
			grid.addColumn(LanguageItemTO::getId).setHeader("Id").setSortProperty("id");
		}

		grid.sort(Arrays.asList(new GridSortOrder<>(contentColumn, SortDirection.ASCENDING)));

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Obsah
		TextField contentFilterField = new TextField();
		contentFilterField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		contentFilterField.setWidth("100%");
		contentFilterField.addValueChangeListener(e -> {
			filterTO.setContent(e.getValue());
			populate(grid, filterTO);
		});
		filteringHeader.getCell(contentColumn).setComponent(contentFilterField);

		// Překlad
		TextField translationFilterField = new TextField();
		translationFilterField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		translationFilterField.setWidth("100%");
		translationFilterField.addValueChangeListener(e -> {
			filterTO.setTranslation(e.getValue());
			populate(grid, filterTO);
		});
		filteringHeader.getCell(translationColumn).setComponent(translationFilterField);

		populate(grid, filterTO);

		pageLayout.add(grid);

		if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN))
			pageLayout.add(createButtonLayout(grid, langId, type));
	}

	private void createStatisticsTab(long langId) {
		LanguageItemTO to = new LanguageItemTO();
		to.setLanguage(langId);
		to.setType(ItemType.WORD);
		int words = languageFacade.countLanguageItems(to);
		to.setType(ItemType.PHRASE);
		int phrases = languageFacade.countLanguageItems(to);

		pageLayout.add("Slovíček: " + words);
		final BufferedImage wordsImage = ChartUtils.drawChart(languageFacade.getStatisticsItems(ItemType.WORD, langId));
		pageLayout.add(new Image(new StreamResource("words", () -> {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				ImageIO.write(wordsImage, "png", os);
				return new ByteArrayInputStream(os.toByteArray());
			} catch (IOException e) {
				logger.error("Nezdařilo se vytváření grafu statistiky", e);
				return null;
			}
		}), "wordsImage.png"));

		pageLayout.add("Frází: " + phrases);
		final BufferedImage phrasesImage = ChartUtils
				.drawChart(languageFacade.getStatisticsItems(ItemType.PHRASE, langId));
		pageLayout.add(new Image(new StreamResource("phrases", () -> {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				ImageIO.write(phrasesImage, "png", os);
				return new ByteArrayInputStream(os.toByteArray());
			} catch (IOException e) {
				logger.error("Nezdařilo se vytváření grafu statistiky", e);
				return null;
			}
		}), "phrasesImage.png"));

		pageLayout.add("Položek celkem: " + (words + phrases));

		final BufferedImage itemsImage = ChartUtils.drawChart(languageFacade.getStatisticsItems(null, langId));
		pageLayout.add(new Image(new StreamResource("items", () -> {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				ImageIO.write(itemsImage, "png", os);
				return new ByteArrayInputStream(os.toByteArray());
			} catch (IOException e) {
				logger.error("Nezdařilo se vytváření grafu statistiky", e);
				return null;
			}
		}), "itemsImage.png"));
	}

	private ButtonLayout createButtonLayout(Grid<LanguageItemTO> grid, long langId, ItemType type) {
		ButtonLayout btnLayout = new ButtonLayout();

		btnLayout.add(new CreateGridButton("Přidat", event -> new LanguageItemDialog(to -> {
			to.setLanguage(langId);
			languageFacade.saveLanguageItem(to);
			grid.getDataProvider().refreshAll();
		}, langId, type).open()));

		btnLayout.add(new ModifyGridButton<LanguageItemTO>("Upravit", item -> {
			ItemType oldType = item.getType();
			new LanguageItemDialog(item, to -> {
				languageFacade.saveLanguageItem(to);
				if (oldType.equals(to.getType()))
					grid.getDataProvider().refreshItem(to);
				else
					grid.getDataProvider().refreshAll();
			}, langId, type).open();
		}, grid));

		btnLayout.add(new DeleteGridButton<LanguageItemTO>("Odstranit", items -> items.forEach(item -> {
			languageFacade.deleteLanguageItem(item);
			grid.getDataProvider().refreshAll();
		}), grid));

		GridButton<LanguageItemTO> moveBtn = new GridButton<>("Změnit jazyk", items -> changeLangOfItems(items, grid),
				grid);
		moveBtn.setIcon(new Image(ImageIcon.MOVE_16_ICON.createResource(), "move"));
		btnLayout.add(moveBtn);

		String caption;
		if (ItemType.WORD == type)
			caption = "slovíček";
		else if (ItemType.PHRASE == type)
			caption = "frází";
		else
			caption = "všeho";

		Button testBtn = new Button("Spustit test " + caption, event -> {
			tabs.setSelectedIndex(3);
			startTest(langId, type);
		});
		testBtn.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "test"));
		btnLayout.add(testBtn);

		if (type != null) {
			Float wordsProgress = languageFacade.getSuccessRateOfLanguageAndType(type, langId);
			ProgressBar wordsSuccessBar = new ProgressBar(0, 100);
			wordsSuccessBar.setValue(wordsProgress);
			btnLayout.add(wordsSuccessBar);

			Span wordsProgressLabel = new Span((int) (wordsProgress * 100) + "%");
			btnLayout.add(wordsProgressLabel);
			wordsSuccessBar.setWidth("200px");
		}

		return btnLayout;
	}

	private void changeLangOfItems(Set<LanguageItemTO> items, Grid<LanguageItemTO> grid) {
		Dialog w = new WebDialog("Změna jazyka");

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		w.add(layout);

		List<LanguageTO> langs = languageFacade.getLanguages();
		Grid<LanguageTO> targatGrid = new Grid<>(LanguageTO.class);
		targatGrid.setItems(langs);
		targatGrid.addColumn(LanguageTO::getName).setHeader("Název");
		layout.add(targatGrid);

		targatGrid.addSelectionListener(se -> se.getFirstSelectedItem().ifPresent(lang -> items.forEach(item -> {
			languageFacade.moveLanguageItemTo(item, lang);
			targatGrid.getDataProvider().refreshAll();
			w.close();
			grid.getDataProvider().refreshAll();
		})));
		w.open();
	}

}
