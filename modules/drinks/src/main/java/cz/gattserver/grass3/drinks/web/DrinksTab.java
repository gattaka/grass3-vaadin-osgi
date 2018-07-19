package cz.gattserver.grass3.drinks.web;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.teemu.ratingstars.RatingStars;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.TextRenderer;
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
import cz.gattserver.web.common.ui.ImageIcon;

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
	private Embedded image;
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
		Column<DrinkOverviewTO, DrinkType> authorColumn = grid.addColumn(DrinkOverviewTO::getType)
				.setRenderer(DrinkType::getCaption, new TextRenderer()).setCaption("Typ").setWidth(100);
		grid.addColumn(to -> {
			RatingStars rs = new RatingStars();
			rs.setValue(to.getRating());
			rs.setReadOnly(true);
			return rs;
		}).setRenderer(new ComponentRenderer()).setCaption("Hodnocení");
		grid.setWidth("438px");
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
		panel.setWidth("520px");
		panel.setHeight("100%");
		mainLayout.addComponent(panel);
		mainLayout.setExpandRatio(panel, 1);

		nameLabel = new H2Label();
		contentLayout.addComponent(nameLabel);

		image = new Embedded(null, ImageIcon.BUBBLE_16_ICON.createResource());
		contentLayout.addComponent(image);
		contentLayout.setComponentAlignment(image, Alignment.TOP_CENTER);

		descriptionLabel = new Label();
		contentLayout.addComponent(descriptionLabel);

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
		this.choosenDrink = choosenDrink;
		if (choosenDrink == null) {
			nameLabel.setValue(null);
			descriptionLabel.setValue(null);
			String currentURL = request.getContextRoot() + "/" + pageFactory.getPageName();
			Page.getCurrent().pushState(currentURL);
		} else {
			nameLabel.setValue(choosenDrink.getName());
			byte[] co = choosenDrink.getImage();
			// https://vaadin.com/forum/thread/260778
			String name = choosenDrink.getName()
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
			image.setSource(new StreamResource(() -> new ByteArrayInputStream(co), name));
			image.markAsDirty();
			descriptionLabel.setValue(choosenDrink.getDescription());

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
