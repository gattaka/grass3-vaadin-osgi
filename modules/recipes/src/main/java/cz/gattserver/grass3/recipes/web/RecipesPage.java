package cz.gattserver.grass3.recipes.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.server.SerializableSupplier;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.FetchItemsCallback;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.model.util.QuerydslUtil;
import cz.gattserver.grass3.recipes.facades.RecipesService;
import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.recipes.model.dto.RecipeOverviewTO;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.H2Label;

public class RecipesPage extends OneColumnPage {

	@Autowired
	private SecurityService securityService;

	private transient RecipesService recipesService;

	private Grid<RecipeOverviewTO> grid;
	private Label nameLabel;
	private Label contentLabel;
	private RecipeDTO choosenRecipe;
	private RecipeOverviewTO filterTO;

	public RecipesPage(GrassRequest request) {
		super(request);
	}

	private void showDetail(RecipeDTO choosenRecipe) {
		nameLabel.setValue(choosenRecipe.getName());
		contentLabel.setValue(getRecipesService().eolToBreakline(choosenRecipe.getDescription()));
		this.choosenRecipe = choosenRecipe;
	}

	@Override
	protected Component createColumnContent() {
		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setPadding(true);

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setPadding(true);
		marginLayout.addComponent(layout);

		HorizontalLayout recipesLayout = new HorizontalLayout();
		recipesLayout.setWidth("100%");
		layout.addComponent(recipesLayout);

		filterTO = new RecipeOverviewTO();

		grid = new Grid<>();
		Column<RecipeOverviewTO, String> nazevColumn = grid.addColumn(RecipeOverviewTO::getName).setCaption("Název");
		grid.setWidth("358px");
		grid.setHeight("600px");
		recipesLayout.addComponent(grid);

		grid.addSelectionListener((e) -> e.getFirstSelectedItem()
				.ifPresent((v) -> showDetail(getRecipesService().getRecipeById(v.getId()))));

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			populate();
		});
		filteringHeader.getCell(nazevColumn).setComponent(nazevColumnField);

		populate();

		VerticalLayout contentLayout = new VerticalLayout();
		Panel panel = new Panel(contentLayout);
		panel.setSizeFull();
		recipesLayout.addComponent(panel);
		recipesLayout.setExpandRatio(panel, 1);

		nameLabel = new H2Label();
		contentLayout.addComponent(nameLabel);

		contentLabel = new Label();
		contentLabel.setWidth("100%");
		contentLabel.setContentMode(ContentMode.HTML);
		contentLayout.addComponent(contentLabel);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		layout.addComponent(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN));

		btnLayout.addComponent(new CreateGridButton("Přidat", event -> {
			UI.getCurrent().addWindow(new RecipeWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(String name, String desc, Long id) {
					id = getRecipesService().saveRecipe(name, desc);
					RecipeDTO to = new RecipeDTO(id, name, desc);
					showDetail(to);
					populate();
				}
			});
		}));

		btnLayout.addComponent(new ModifyGridButton<RecipeOverviewTO>("Upravit", event -> {
			UI.getCurrent().addWindow(new RecipeWindow(choosenRecipe) {
				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(String name, String desc, Long id) {
					getRecipesService().saveRecipe(name, desc, id);
					RecipeDTO to = new RecipeDTO(id, name, desc);
					showDetail(to);
					populate();
				}
			});
		}, grid));

		return marginLayout;
	}

	private RecipesService getRecipesService() {
		if (recipesService == null)
			recipesService = SpringContextHelper.getBean(RecipesService.class);
		return recipesService;
	}

	private void populate() {
		FetchItemsCallback<RecipeOverviewTO> fetchItems = (sortOrder, offset, limit) -> getRecipesService()
				.getRecipes(filterTO.getName(), QuerydslUtil.transformOffsetLimit(offset, limit)).stream();
		SerializableSupplier<Integer> sizeCallback = () -> getRecipesService().getRecipesCount(filterTO.getName());
		CallbackDataProvider<RecipeOverviewTO, Long> provider = new CallbackDataProvider<>(
				q -> fetchItems.fetchItems(q.getSortOrders(), q.getOffset(), q.getLimit()), q -> sizeCallback.get(),
				RecipeOverviewTO::getId);
		grid.setDataProvider(provider);
	}
}
