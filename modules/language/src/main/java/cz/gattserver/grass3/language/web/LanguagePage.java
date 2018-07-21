package cz.gattserver.grass3.language.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.ValidationResult;
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
import com.vaadin.ui.Slider;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.language.facades.LanguageFacade;
import cz.gattserver.grass3.language.model.domain.ItemType;
import cz.gattserver.grass3.language.model.dto.CrosswordCell;
import cz.gattserver.grass3.language.model.dto.CrosswordHintTO;
import cz.gattserver.grass3.language.model.dto.CrosswordTO;
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.language.model.dto.LanguageTO;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.ui.BoldLabel;
import cz.gattserver.web.common.ui.ImageIcon;

public class LanguagePage extends OneColumnPage {

	private static final String PREKLAD_LABEL = "Překlad";

	@Autowired
	private LanguageFacade languageFacade;

	@Autowired
	private SecurityService securityService;

	private TabSheet tabSheet;

	public LanguagePage(GrassRequest request) {
		super(request);
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
		if (securityService.getCurrentUser().getRoles().contains(Role.ADMIN))
			langLayout.addComponent(btnLayout);

		grid.addSelectionListener(se -> se.getFirstSelectedItem().ifPresent(item -> {
			if (tabSheet != null)
				langLayout.removeComponent(tabSheet);

			long langId = item.getId();
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
						newTab = createItemsTab(langId, ItemType.WORD);
						break;
					case 1:
						newTab = createItemsTab(langId, ItemType.PHRASE);
						break;
					case 2:
						newTab = createItemsTab(langId, null);
						break;
					default:
						break;
					}
					tabSheet.replaceComponent(selectedTab, newTab);
				}
			});

			tabSheet.addTab(createItemsTab(langId, ItemType.WORD), "Slovíčka");
			tabSheet.addTab(new VerticalLayout(), "Fráze");
			tabSheet.addTab(new VerticalLayout(), "Vše");
			if (securityService.getCurrentUser().getRoles().contains(Role.ADMIN))
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

		return layout;
	}

	private Component createCrosswordTab(long langId) {
		VerticalLayout sheet = new VerticalLayout();
		sheet.setMargin(new MarginInfo(true, false, false, false));

		HorizontalLayout btnLayout = new HorizontalLayout();
		sheet.addComponent(btnLayout);

		Map<TextField, String> fieldMap = new HashMap<>();

		Button giveUpTestBtn = new Button("Vzdát to", event -> {
			for (TextField tf : fieldMap.keySet())
				tf.setValue(fieldMap.get(tf));
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
		// hintsLayout.setColumnExpandRatio(columnIndex, ratio);
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

		GridLayout crosswordLayout = new GridLayout(crosswordTO.getWidth(), crosswordTO.getHeight());
		crosswordLayout.setMargin(new MarginInfo(false, false, true, false));
		crosswordLayout.setSpacing(false);

		mainLayout.addComponent(crosswordLayout);
		mainLayout.setComponentAlignment(crosswordLayout, Alignment.MIDDLE_CENTER);
		mainLayout.addComponent(hintsLayout);

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

						// logika pro zapipsování skrz postranní pole
						for (CrosswordField cf : writeFields)
							cf.tryRegisterCellField(t, x, y);

						// logika pro kontrolu správného výsledku
						fieldMap.put(t, cell.getValue());
						t.addValueChangeListener(e -> {
							for (TextField tf : fieldMap.keySet()) {
								String is = tf.getValue();
								String shouldBe = fieldMap.get(tf);
								if (StringUtils.isNotBlank(shouldBe) && !shouldBe.toLowerCase().equals(is.toLowerCase())
										|| StringUtils.isBlank(shouldBe) && StringUtils.isNotBlank(is))
									return;
							}
							for (TextField tf : fieldMap.keySet()) {
								tf.addStyleName("crossword-done");
								tf.setEnabled(false);
							}

						});
					}
					crosswordLayout.addComponent(t, x, y);
				}
			}
		}
	}

	private VerticalLayout createTestTab(Long langId) {
		VerticalLayout sheet = new VerticalLayout();
		sheet.setMargin(new MarginInfo(true, false, false, false));

		HorizontalLayout btnLayout = new HorizontalLayout();
		sheet.addComponent(btnLayout);

		VerticalLayout testLayout = new VerticalLayout();
		testLayout.setMargin(false);
		testLayout.addComponent(new Label("Vyberte test"));
		sheet.addComponent(testLayout);

		Button wordsTestBtn = new Button("Spustit test slovíček",
				event -> startTest(langId, ItemType.WORD, testLayout));
		wordsTestBtn.setIcon(ImageIcon.RIGHT_16_ICON.createResource());
		btnLayout.addComponent(wordsTestBtn);

		Button phrasesTestBtn = new Button("Spustit test frází",
				event -> startTest(langId, ItemType.PHRASE, testLayout));
		phrasesTestBtn.setIcon(ImageIcon.RIGHT_16_ICON.createResource());
		btnLayout.addComponent(phrasesTestBtn);

		Button allTestBtn = new Button("Spustit test všeho", event -> startTest(langId, null, testLayout));
		allTestBtn.setIcon(ImageIcon.RIGHT_16_ICON.createResource());
		btnLayout.addComponent(allTestBtn);

		return sheet;
	}

	private void startTest(Long langId, ItemType type, VerticalLayout testLayout) {
		testLayout.removeAllComponents();

		Map<LanguageItemTO, TextField> answersMap = new LinkedHashMap<>();

		List<LanguageItemTO> items = languageFacade.getLanguageItemsForTest(langId, type);
		if (items.isEmpty()) {
			testLayout.addComponent(new Label("Není z čeho zkoušet"));
			return;
		}

		GridLayout gridLayout = new GridLayout(2, items.size());
		gridLayout.setSpacing(true);
		gridLayout.setMargin(new MarginInfo(true, false, false, false));
		gridLayout.setWidth("100%");
		gridLayout.setColumnExpandRatio(1, 1);
		testLayout.addComponent(gridLayout);

		gridLayout.addComponent(new BoldLabel("Položka"));
		gridLayout.addComponent(new BoldLabel(PREKLAD_LABEL));

		for (LanguageItemTO item : items) {
			Label label = new Label(item.getTranslation());
			label.setWidth(null);
			gridLayout.addComponent(label);

			TextField answerField = new TextField();
			answerField.setWidth("100%");
			answerField.setPlaceholder("varianta;varianta;...");
			gridLayout.addComponent(answerField);

			answersMap.put(item, answerField);
		}

		Button submitBtn = new Button("Zkontrolovat");
		submitBtn.addClickListener(e -> {
			testLayout.removeAllComponents();

			GridLayout resultLayout = new GridLayout(4, items.size());
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

		if (securityService.getCurrentUser().getRoles().contains(Role.ADMIN)) {
			grid.addColumn(item -> (Math.floor(item.getSuccessRate() * 1000) / 10) + "%").setCaption("Úspěšnost")
					.setStyleGenerator(item -> "v-align-right").setSortProperty("successRate");
			grid.addColumn(LanguageItemTO::getLastTested, new LocalDateTimeRenderer("dd.MM.yyyy HH:mm"))
					.setCaption("Naposledy zkoušeno").setStyleGenerator(item -> "v-align-right")
					.setSortProperty("lastTested");
			grid.addColumn(LanguageItemTO::getTested).setCaption("Zkoušeno").setSortProperty("tested");
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

		if (securityService.getCurrentUser().getRoles().contains(Role.ADMIN))
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
				}, (value, context) -> {
					LanguageItemTO itemTO = languageFacade.getLanguageItemByContent(langId, value);
					return itemTO == null ? ValidationResult.ok() : ValidationResult.error("Položka již existuje");
				}, type))));

		btnLayout.addComponent(new ModifyGridButton<LanguageItemTO>("Upravit", item -> {
			ItemType oldType = item.getType();
			UI.getCurrent().addWindow(new LanguageItemWindow(item, to -> {
				languageFacade.saveLanguageItem(to);
				if (oldType.equals(to.getType()))
					grid.getDataProvider().refreshItem(to);
				else
					grid.getDataProvider().refreshAll();
			}, (value, context) -> {
				LanguageItemTO itemTO = languageFacade.getLanguageItemByContent(langId, value);
				if (itemTO == null || itemTO.getContent().equals(item.getContent()))
					return ValidationResult.ok();
				else
					return ValidationResult.error("Položka již existuje");
			}, type));
		}, grid));

		btnLayout.addComponent(new DeleteGridButton<LanguageItemTO>("Odstranit", items -> items.forEach(item -> {
			languageFacade.deleteLanguageItem(item);
			grid.getDataProvider().refreshAll();
		}), grid));

		return btnLayout;
	}

}
