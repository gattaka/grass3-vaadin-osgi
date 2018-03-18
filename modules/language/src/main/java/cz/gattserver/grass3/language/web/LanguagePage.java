package cz.gattserver.grass3.language.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.language.facades.LanguageFacade;
import cz.gattserver.grass3.language.model.domain.ItemType;
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.language.model.dto.LanguageTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.GridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.ui.BoldLabel;
import cz.gattserver.web.common.ui.ImageIcon;

public class LanguagePage extends OneColumnPage {

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

		GridButton<LanguageTO> chooseBtn = new GridButton<>("Vybrat", item -> {
			if (tabSheet != null)
				langLayout.removeComponent(tabSheet);

			long langId = item.iterator().next().getId();
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
					default:
						break;
					}
					tabSheet.replaceComponent(selectedTab, newTab);
				}
			});

			tabSheet.addTab(createTestTab(langId), "Zkoušení");
			tabSheet.addTab(createItemsTab(langId, ItemType.WORD), "Slovíčka");
			tabSheet.addTab(createItemsTab(langId, ItemType.PHRASE), "Fráze");

			langLayout.addComponent(tabSheet);
		}, grid);
		chooseBtn.setIcon(ImageIcon.RIGHT_16_ICON.createResource());
		btnLayout.addComponent(chooseBtn);

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
		gridLayout.addComponent(new BoldLabel("Překlad"));

		for (LanguageItemTO item : items) {
			Label label = new Label(item.getTranslation());
			label.setWidth(null);
			gridLayout.addComponent(label);

			TextField answerField = new TextField();
			answerField.setWidth("100%");
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
			resultLayout.addComponent(new BoldLabel("Překlad"));
			resultLayout.addComponent(new BoldLabel("Odpověď"));

			answersMap.keySet().forEach(item -> {
				TextField answerField = answersMap.get(item);
				String answer = answerField.getValue();
				String correctAnswer = item.getContent().toLowerCase().trim();

				boolean success = answer != null && answer.toLowerCase().trim().equals(correctAnswer);
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

	private VerticalLayout createItemsTab(Long langId, ItemType type) {
		VerticalLayout sheet = new VerticalLayout();
		sheet.setMargin(new MarginInfo(true, false, false, false));

		Grid<LanguageItemTO> grid = new Grid<>();
		grid.setWidth("100%");
		grid.setHeight("500px");
		grid.setDataProvider(
				(sortOrder, offset, limit) -> languageFacade.getLanguageItems(langId, type, offset, limit).stream(),
				() -> languageFacade.countLanguageItems(langId, type));
		grid.addColumn(LanguageItemTO::getContent).setCaption("Obsah");
		grid.addColumn(LanguageItemTO::getTranslation).setCaption("Překlad");
		grid.addColumn(item -> (Math.floor(item.getSuccessRate() * 1000) / 10) + "%").setCaption("Úspěšnost")
				.setStyleGenerator(item -> "v-align-right");
		grid.addColumn(LanguageItemTO::getLastTested, new LocalDateTimeRenderer("dd.MM.yyyy HH:mm"))
				.setCaption("Naposledy zkoušeno").setStyleGenerator(item -> "v-align-right");
		grid.addColumn(LanguageItemTO::getTested).setCaption("Zkoušeno");
		sheet.addComponent(grid);

		sheet.addComponent(createButtonLayout(grid, langId, type));

		return sheet;
	}

	private HorizontalLayout createButtonLayout(Grid<LanguageItemTO> grid, long langId, ItemType type) {
		HorizontalLayout btnLayout = new HorizontalLayout();

		btnLayout.addComponent(
				new CreateGridButton("Přidat", event -> UI.getCurrent().addWindow(new LanguageItemWindow(to -> {
					to.setLanguage(langId);
					to.setType(type);
					languageFacade.saveLanguageItem(to);
					grid.getDataProvider().refreshAll();
				}))));

		btnLayout.addComponent(new ModifyGridButton<LanguageItemTO>("Upravit",
				item -> UI.getCurrent().addWindow(new LanguageItemWindow(item, to -> {
					languageFacade.saveLanguageItem(to);
					grid.getDataProvider().refreshItem(to);
				})), grid));

		return btnLayout;
	}

}
