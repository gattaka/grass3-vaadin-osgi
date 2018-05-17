package cz.gattserver.grass3.language.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.ValidationResult;
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
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.language.model.dto.LanguageTO;
import cz.gattserver.grass3.server.GrassRequest;
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

	private TabSheet tabSheet;

	public LanguagePage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		VerticalLayout langLayout = new VerticalLayout();
		layout.addComponent(langLayout);

		List<LanguageTO> langs = languageFacade.getLanguages();
		Grid<LanguageTO> grid = new Grid<>("Jazyky", langs);
		grid.setWidth("100%");
		grid.setHeight("150px");
		grid.addColumn(LanguageTO::getName).setCaption("Název");
		langLayout.addComponent(grid);

		HorizontalLayout btnLayout = new HorizontalLayout();
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
				if (pos > 0) {
					Component newTab = null;
					switch (pos) {
					case 1:
						newTab = createItemsTab(langId, ItemType.WORD);
						break;
					case 2:
						newTab = createItemsTab(langId, ItemType.PHRASE);
						break;
					case 3:
						newTab = createItemsTab(langId, null);
						break;
					default:
						break;
					}
					tabSheet.replaceComponent(selectedTab, newTab);
				}
			});

			tabSheet.addTab(createTestTab(langId), "Zkoušení");
			tabSheet.addTab(new VerticalLayout(), "Slovíčka");
			tabSheet.addTab(new VerticalLayout(), "Fráze");
			tabSheet.addTab(new VerticalLayout(), "Vše");

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
		grid.addColumn(item -> (Math.floor(item.getSuccessRate() * 1000) / 10) + "%").setCaption("Úspěšnost")
				.setStyleGenerator(item -> "v-align-right").setSortProperty("successRate");
		grid.addColumn(LanguageItemTO::getLastTested, new LocalDateTimeRenderer("dd.MM.yyyy HH:mm"))
				.setCaption("Naposledy zkoušeno").setStyleGenerator(item -> "v-align-right")
				.setSortProperty("lastTested");
		grid.addColumn(LanguageItemTO::getTested).setCaption("Zkoušeno").setSortProperty("tested");

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
