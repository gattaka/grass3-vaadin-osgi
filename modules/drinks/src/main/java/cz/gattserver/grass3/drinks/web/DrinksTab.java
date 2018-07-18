package cz.gattserver.grass3.drinks.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.drinks.facades.DrinksFacade;
import cz.gattserver.grass3.drinks.model.domain.DrinkType;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkTO;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.H2Label;

public class DrinksTab extends VerticalLayout {

	private static final long serialVersionUID = 594189301140808163L;

	@Autowired
	private DrinksFacade drinksFacade;

	@Autowired
	private SecurityService securityService;

	@Resource(name = "drinksPageFactory")
	private PageFactory pageFactory;

	private GrassRequest request;

	private Grid<DrinkOverviewTO> grid;
	private Label nameLabel;
	private Label typeLabel;
	private Label ratingLabel;
	private Label descriptionLabel;

	private DrinkTO choosenDrink;
	private List<DrinkOverviewTO> drinks;
	private DrinkOverviewTO filterTO;

	public DrinksTab(GrassRequest request) {
		SpringContextHelper.inject(this);
		setMargin(new MarginInfo(true, false, false, false));

		this.request = request;

		drinks = new ArrayList<>();
		filterTO = new DrinkOverviewTO();

		HorizontalLayout mainLayout = new HorizontalLayout();
		addComponent(mainLayout);

		grid = new Grid<>(null, drinks);
		Column<DrinkOverviewTO, String> nazevColumn = grid.addColumn(DrinkOverviewTO::getName).setCaption("Název");
		Column<DrinkOverviewTO, DrinkType> authorColumn = grid.addColumn(DrinkOverviewTO::getType).setCaption("Typ")
				.setWidth(150);
		grid.addColumn(DrinkOverviewTO::getRating).setCaption("Hodnocení");
		grid.setWidth("398px");
		grid.setHeight("600px");
		mainLayout.addComponent(grid);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			loadDrinks();
		});
		filteringHeader.getCell(nazevColumn).setComponent(nazevColumnField);

		// Typ
		ComboBox<DrinkType> typeColumnField = new ComboBox<>(null, Arrays.asList(DrinkType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addStyleName(ValoTheme.COMBOBOX_TINY);
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setType(e.getValue());
			loadDrinks();
		});
		typeColumnField.setItemCaptionGenerator(DrinkType::getCaption);
		filteringHeader.getCell(authorColumn).setComponent(typeColumnField);

		loadDrinks();

		grid.addSelectionListener((e) -> {
			if (e.getFirstSelectedItem().isPresent())
				showDetail(drinksFacade.getDrinkById(e.getFirstSelectedItem().get().getId()));
			else
				showDetail(null);
		});

		VerticalLayout contentLayout = new VerticalLayout();

		Panel panel = new Panel(contentLayout);
		panel.setWidth("560px");
		panel.setHeight("100%");
		mainLayout.addComponent(panel);
		mainLayout.setExpandRatio(panel, 1);

		nameLabel = new H2Label();
		contentLayout.addComponent(nameLabel);

		typeLabel = new Label();
		contentLayout.addComponent(typeLabel);

		descriptionLabel = new Label();
		contentLayout.addComponent(descriptionLabel);

		ratingLabel = new Label();
		ratingLabel.setWidth("520px");
		contentLayout.addComponent(ratingLabel);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		addComponent(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(Role.ADMIN));

		btnLayout.addComponent(new CreateGridButton("Přidat", event -> {
			UI.getCurrent().addWindow(new DrinkWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(DrinkTO to) {
					to = drinksFacade.saveDrink(to);
					showDetail(to);
					loadDrinks();
				}
			});
		}));

		btnLayout.addComponent(new ModifyGridButton<DrinkOverviewTO>("Upravit", event -> {
			UI.getCurrent().addWindow(new DrinkWindow(choosenDrink) {

				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(DrinkTO to) {
					to = drinksFacade.saveDrink(to);
					showDetail(to);
					loadDrinks();
				}
			});
		}, grid));

		btnLayout.addComponent(new DeleteGridButton<DrinkOverviewTO>("Smazat", items -> {
			for (DrinkOverviewTO s : items)
				drinksFacade.deleteDrink(s.getId());
			loadDrinks();
			showDetail(null);
		}, grid));

	}

	public void selectDrink(Long id) {
		int row = 0;
		for (DrinkOverviewTO to : drinks) {
			if (to.getId().equals(id)) {
				grid.select(to);
				grid.scrollTo(row, ScrollDestination.MIDDLE);
			}
			row++;
		}
	}

	private void showDetail(DrinkTO choosenDrink) {
		if (choosenDrink == null) {
			nameLabel.setValue(null);
			ratingLabel.setValue(null);
			descriptionLabel.setValue(null);
			typeLabel.setValue(null);
			this.choosenDrink = null;
			String currentURL = request.getContextRoot() + "/" + pageFactory.getPageName();
			Page.getCurrent().pushState(currentURL);
		} else {
			nameLabel.setValue(choosenDrink.getName());
			ratingLabel.setValue(String.valueOf(choosenDrink.getRating()));
			typeLabel.setValue(choosenDrink.getType().getCaption());
			descriptionLabel.setValue(choosenDrink.getDescription());
			this.choosenDrink = choosenDrink;

			String currentURL;
			try {
				currentURL = request.getContextRoot() + "/" + pageFactory.getPageName() + "/drink/"
						+ choosenDrink.getId() + "-" + URLEncoder.encode(choosenDrink.getName(), "UTF-8");
				Page.getCurrent().pushState(currentURL);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

	}

	private void loadDrinks() {
		drinks.clear();
		drinks.addAll(drinksFacade.getDrinks(filterTO));
		grid.getDataProvider().refreshAll();
	}

}
