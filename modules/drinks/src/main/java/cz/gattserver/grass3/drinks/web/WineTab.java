package cz.gattserver.grass3.drinks.web;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.drinks.facades.DrinksFacade;
import cz.gattserver.grass3.drinks.model.domain.WineType;
import cz.gattserver.grass3.drinks.model.interfaces.WineOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineTO;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.BoldLabel;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.ui.ImageIcon;

public class WineTab extends VerticalLayout {

	private static final long serialVersionUID = 594189301140808163L;

	@Autowired
	private DrinksFacade drinksFacade;

	@Autowired
	private SecurityService securityService;

	@Resource(name = "drinksPageFactory")
	private PageFactory pageFactory;

	private GrassRequest request;

	private Grid<WineOverviewTO> grid;
	private Embedded image;
	private VerticalLayout dataLayout;

	private WineTO choosenDrink;
	private List<WineOverviewTO> drinks;
	private WineOverviewTO filterTO;

	public WineTab(GrassRequest request) {
		SpringContextHelper.inject(this);
		setMargin(new MarginInfo(true, false, false, false));

		this.request = request;

		filterTO = new WineOverviewTO();

		grid = new Grid<>();
		Column<WineOverviewTO, String> wineryColumn = grid.addColumn(WineOverviewTO::getWinery).setCaption("Vinařství");
		Column<WineOverviewTO, String> nameColumn = grid.addColumn(WineOverviewTO::getName).setCaption("Název");
		Column<WineOverviewTO, String> countryColumn = grid.addColumn(WineOverviewTO::getCountry).setCaption("Země");
		Column<WineOverviewTO, Double> alcoholColumn = grid.addColumn(WineOverviewTO::getAlcohol)
				.setRenderer(new NumberRenderer(NumberFormat.getNumberInstance(new Locale("cs", "CZ"))))
				.setCaption("Alkohol (%)").setWidth(80);
		Column<WineOverviewTO, Integer> yearsColumn = grid.addColumn(WineOverviewTO::getYear).setCaption("Rok")
				.setWidth(90);
		Column<WineOverviewTO, WineType> WineTypeColumn = grid.addColumn(WineOverviewTO::getWineType)
				.setRenderer(WineType::getCaption, new TextRenderer()).setCaption("Typ vína").setWidth(100);
		grid.addColumn(to -> {
			RatingStars rs = new RatingStars();
			rs.setValue(to.getRating());
			rs.setReadOnly(true);
			return rs;
		}).setRenderer(new ComponentRenderer()).setCaption("Hodnocení").setWidth(120);
		grid.setWidth("100%");
		grid.setHeight("400px");
		addComponent(grid);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Vinařství
		TextField wineryColumnField = new TextField();
		wineryColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		wineryColumnField.setWidth("100%");
		wineryColumnField.addValueChangeListener(e -> {
			filterTO.setWinery(e.getValue());
			populate();
		});
		filteringHeader.getCell(wineryColumn).setComponent(wineryColumnField);

		// Země původu
		TextField countryColumnField = new TextField();
		countryColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		countryColumnField.setWidth("100%");
		countryColumnField.addValueChangeListener(e -> {
			filterTO.setCountry(e.getValue());
			populate();
		});
		filteringHeader.getCell(countryColumn).setComponent(countryColumnField);

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			populate();
		});
		filteringHeader.getCell(nameColumn).setComponent(nazevColumnField);

		// Alkohol
		TextField alcoholColumnField = new TextField();
		alcoholColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		alcoholColumnField.setWidth("100%");
		alcoholColumnField.addValueChangeListener(e -> {
			try {
				filterTO.setAlcohol(Double.parseDouble(e.getValue()));
			} catch (Exception ex) {
			}
			populate();
		});
		filteringHeader.getCell(alcoholColumn).setComponent(alcoholColumnField);

		// Rok
		TextField yearsColumnField = new TextField();
		yearsColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		yearsColumnField.setWidth("100%");
		yearsColumnField.addValueChangeListener(e -> {
			try {
				filterTO.setYear(Integer.parseInt(e.getValue()));
			} catch (Exception ex) {
			}
			populate();
		});
		filteringHeader.getCell(yearsColumn).setComponent(yearsColumnField);

		// Typ vína
		ComboBox<WineType> typeColumnField = new ComboBox<>(null, Arrays.asList(WineType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addStyleName(ValoTheme.COMBOBOX_TINY);
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setWineType(e.getValue());
			populate();
		});
		typeColumnField.setItemCaptionGenerator(WineType::getCaption);
		filteringHeader.getCell(WineTypeColumn).setComponent(typeColumnField);

		populate();

		grid.addSelectionListener((e) -> {
			if (e.getFirstSelectedItem().isPresent())
				showDetail(drinksFacade.getWineById(e.getFirstSelectedItem().get().getId()));
			else
				showDetail(null);
		});

		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.setMargin(true);

		Panel panel = new Panel(contentLayout);
		panel.setWidth("100%");
		panel.setHeight("100%");
		addComponent(panel);
		setExpandRatio(panel, 1);

		// musí tady něco být nahrané, jinak to pak nejde měnit (WTF?!)
		image = new Embedded(null, ImageIcon.BUBBLE_16_ICON.createResource());
		image.setVisible(false);
		contentLayout.addComponent(image);
		contentLayout.setComponentAlignment(image, Alignment.TOP_CENTER);

		dataLayout = new VerticalLayout();
		dataLayout.setWidth("100%");
		dataLayout.setMargin(false);
		contentLayout.addComponent(dataLayout);
		contentLayout.setExpandRatio(dataLayout, 1);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		addComponent(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(Role.ADMIN));

		btnLayout.addComponent(new CreateGridButton("Přidat", event -> {
			UI.getCurrent().addWindow(new WineWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(WineTO to) {
					to = drinksFacade.saveWine(to);
					showDetail(to);
					populate();
				}
			});
		}));

		btnLayout.addComponent(new ModifyGridButton<WineOverviewTO>("Upravit", event -> {
			UI.getCurrent().addWindow(new WineWindow(choosenDrink) {
				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(WineTO to) {
					to = drinksFacade.saveWine(to);
					showDetail(to);
					populate();
				}
			});
		}, grid));

		btnLayout.addComponent(new DeleteGridButton<WineOverviewTO>("Smazat", items -> {
			for (WineOverviewTO s : items)
				drinksFacade.deleteDrink(s.getId());
			populate();
			showDetail(null);
		}, grid));

	}

	public void selectDrink(Long id) {
		int row = 0;
		for (WineOverviewTO to : drinks) {
			if (to.getId().equals(id)) {
				grid.select(to);
				grid.scrollTo(row, ScrollDestination.MIDDLE);
			}
			row++;
		}
	}

	private void showDetail(WineTO choosenDrink) {
		this.choosenDrink = choosenDrink;
		dataLayout.removeAllComponents();
		if (choosenDrink == null) {
			image.setVisible(false);
			String currentURL = request.getContextRoot() + "/" + pageFactory.getPageName();
			Page.getCurrent().pushState(currentURL);
		} else {
			byte[] co = choosenDrink.getImage();
			if (co != null) {
				// https://vaadin.com/foWine/thread/260778
				String name = choosenDrink.getName()
						+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				image.setVisible(true);
				image.setSource(new StreamResource(() -> new ByteArrayInputStream(co), name));
				image.markAsDirty();
			} else {
				image.setVisible(false);
			}

			H2Label nameLabel = new H2Label(
					choosenDrink.getWinery() + " " + choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")");
			dataLayout.addComponent(nameLabel);

			RatingStars rs = new RatingStars();
			rs.setValue(choosenDrink.getRating());
			rs.setReadOnly(true);
			dataLayout.addComponent(rs);

			GridLayout infoLayout = new GridLayout(2, 7);
			dataLayout.addComponent(infoLayout);

			BoldLabel b;
			infoLayout.addComponent(new BoldLabel("Rok"));
			infoLayout.addComponent(new Label(String.valueOf(choosenDrink.getYear())));
			infoLayout.addComponent(b = new BoldLabel("Alkohol (%)"));
			b.setWidth("120px");
			infoLayout.addComponent(new Label(String.valueOf(choosenDrink.getAlcohol())));
			infoLayout.addComponent(new BoldLabel("Typ vína"));
			infoLayout.addComponent(new Label(choosenDrink.getWineType().getCaption()));

			Label descriptionLabel = new Label(choosenDrink.getDescription());
			dataLayout.addComponent(descriptionLabel);

			String currentURL;
			try {
				currentURL = request.getContextRoot() + "/" + pageFactory.getPageName() + "/wine/"
						+ choosenDrink.getId() + "-" + URLEncoder.encode(choosenDrink.getName(), "UTF-8");
				Page.getCurrent().pushState(currentURL);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

	}

	private void populate() {
		grid.setDataProvider(
				(sortOrder, offset, limit) -> drinksFacade.getWines(filterTO, offset, limit, sortOrder).stream(),
				() -> drinksFacade.countWines(filterTO));
	}

}
