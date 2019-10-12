package cz.gattserver.grass3.language.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass3.language.facades.LanguageFacade;
import cz.gattserver.grass3.language.model.domain.ItemType;
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.language.model.dto.LanguageTO;
import cz.gattserver.grass3.language.web.dialogs.LanguageDialog;
import cz.gattserver.grass3.language.web.tabs.CrosswordTab;
import cz.gattserver.grass3.language.web.tabs.ItemsTab;
import cz.gattserver.grass3.language.web.tabs.StatisticsTab;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.Strong;

@Route("language")
public class LanguagePage extends OneColumnPage {

	private static final long serialVersionUID = 4767207674013382065L;

	public static final String PREKLAD_LABEL = "Překlad";

	@Autowired
	private LanguageFacade languageFacade;

	@Autowired
	private SecurityService securityService;

	private Tabs tabs;
	private Div tabLayout;
	private VerticalLayout testLayout;

	public LanguagePage() {
		init();
	}

	private void createTabSheet(long langId) {
		tabs = new Tabs();
		tabs.addClassName("top-margin");
		tabs.addSelectedChangeListener(e -> {
			tabLayout.removeAll();
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
				createTestTab(langId);
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
		Grid<LanguageTO> grid = new Grid<>();
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
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

		tabLayout = new Div();
		tabLayout.addClassName("top-margin");
		layout.add(tabLayout);

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

		btnLayout.add(new CreateGridButton("Přidat", event -> new LanguageDialog(to -> {
			languageFacade.saveLanguage(to);
			langs.clear();
			langs.addAll(languageFacade.getLanguages());
			grid.getDataProvider().refreshAll();
		}).open()));

		btnLayout.add(new ModifyGridButton<LanguageTO>("Upravit", item -> new LanguageDialog(item, to -> {
			languageFacade.saveLanguage(to);
			langs.clear();
			langs.addAll(languageFacade.getLanguages());
			grid.getDataProvider().refreshAll();
		}).open(), grid));

		if (!langs.isEmpty())
			grid.select(langs.get(0));
	}

	private void createCrosswordTab(Long langId) {
		tabLayout.add(new CrosswordTab(langId));
	}

	private void createTestTab(Long langId) {
		ButtonLayout buttonLayout = new ButtonLayout();
		tabLayout.add(buttonLayout);

		Button allTestBtn = new Button("Spustit test všeho", event -> startTest(langId, null));
		allTestBtn.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "run"));
		buttonLayout.add(allTestBtn);

		Float wordsProgress = languageFacade.getSuccessRateOfLanguageAndType(ItemType.WORD, langId);
		String wordsProgressLabel = (int) (wordsProgress * 100) + "%";
		Button wordsTestBtn = new Button("Spustit test slovíček (" + wordsProgressLabel + ")",
				event -> startTest(langId, ItemType.WORD));
		wordsTestBtn.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "run"));
		buttonLayout.add(wordsTestBtn);

		Float phrasesProgress = languageFacade.getSuccessRateOfLanguageAndType(ItemType.PHRASE, langId);
		String phrasesProgressLabel = (int) (phrasesProgress * 100) + "%";
		Button phrasesTestBtn = new Button("Spustit test frází (" + phrasesProgressLabel + ")",
				event -> startTest(langId, ItemType.PHRASE));
		phrasesTestBtn.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "run"));
		buttonLayout.add(phrasesTestBtn);

		testLayout = new VerticalLayout();
		testLayout.setPadding(false);
		tabLayout.add(testLayout);
	}

	private void createGridLine(LanguageItemTO item, FormLayout gridLayout, Map<LanguageItemTO, TextField> answersMap) {
		Div label = new Div();
		label.add(item.getTranslation());
		label.setWidth(null);
		gridLayout.add(label);

		TextField answerField = new TextField();
		answerField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		answerField.setWidth("100%");
		answerField.setPlaceholder("varianta;varianta;...");
		gridLayout.add(answerField);

		answersMap.put(item, answerField);
	}

	public void startTest(Long langId, ItemType type) {
		testLayout.removeAll();

		Map<LanguageItemTO, TextField> answersMap = new LinkedHashMap<>();

		List<LanguageItemTO> itemsToLearn = languageFacade.getLanguageItemsForTest(langId, 0, 0.1, 10, type);
		List<LanguageItemTO> itemsToImprove = languageFacade.getLanguageItemsForTest(langId, 0.1, 0.8, 5, type);
		List<LanguageItemTO> itemsToRefresh = languageFacade.getLanguageItemsForTest(langId, 0.8, 1.1, 4, type);

		FormLayout columnsLayout = new FormLayout();
		columnsLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("200px", 2));
		columnsLayout.setWidth("100%");
		testLayout.add(columnsLayout);

		Div header = new Div(new Strong("Položka"));
		header.addClassName("top-margin");
		columnsLayout.add(header);
		columnsLayout.add(new Strong(PREKLAD_LABEL));

		header = new Div(new Strong("Nové"));
		header.addClassName("top-margin");
		columnsLayout.add(header, 2);
		for (LanguageItemTO item : itemsToLearn)
			createGridLine(item, columnsLayout, answersMap);

		header = new Div(new Strong("Ke zlepšení"));
		header.addClassName("top-margin");
		columnsLayout.add(header, 2);
		for (LanguageItemTO item : itemsToImprove)
			createGridLine(item, columnsLayout, answersMap);

		header = new Div(new Strong("Opakování"));
		header.addClassName("top-margin");
		columnsLayout.add(header, 2);
		for (LanguageItemTO item : itemsToRefresh)
			createGridLine(item, columnsLayout, answersMap);

		Button submitBtn = new Button("Zkontrolovat");
		submitBtn.addClickListener(e -> {
			testLayout.removeAll();

			FormLayout resultLayout = new FormLayout();
			resultLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("10px", 3));
			resultLayout.setWidth("100%");
			testLayout.add(resultLayout);

			Div resultHeader = new Div(new Strong("Položka"));
			resultHeader.addClassName("top-margin");
			resultHeader.addClassName("bottom-margin");
			resultLayout.add(resultHeader);

			resultHeader = new Div(new Strong(PREKLAD_LABEL));
			resultHeader.addClassName("top-margin");
			resultHeader.addClassName("bottom-margin");
			resultLayout.add(resultHeader);

			resultHeader = new Div(new Strong("Odpověď"));
			resultHeader.addClassName("top-margin");
			resultHeader.addClassName("bottom-margin");
			resultLayout.add(resultHeader);

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

				Div label = new Div();
				label.add(" ");
				label.add(item.getTranslation());
				label.setWidth(null);
				resultLayout.add(label);

				resultLayout.add(new Strong(item.getContent()));

				Div answerDiv = new Div();
				answerDiv.add(new Strong(StringUtils.isBlank(answer) ? "---" : answer));
				answerDiv.getStyle().set("color", success ? "hsl(122, 100%, 33%)" : "hsl(0, 100%, 49%)");
				resultLayout.add(answerDiv);

				languageFacade.updateItemAfterTest(item, success);

			});
		});
		testLayout.add(submitBtn);
	}

	private void createItemsTab(Long langId, ItemType type) {
		tabLayout.add(new ItemsTab(langId, type, this));
	}

	private void createStatisticsTab(Long langId) {
		tabLayout.add(new StatisticsTab(langId));
	}

	public Tabs getTabs() {
		return tabs;
	}

}
