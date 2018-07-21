package cz.gattserver.grass3.drinks.web;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.teemu.ratingstars.RatingStars;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
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
import cz.gattserver.grass3.drinks.model.domain.MaltType;
import cz.gattserver.grass3.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;
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

public class BeersTab extends VerticalLayout {

	private static final long serialVersionUID = 594189301140808163L;

	@Autowired
	private DrinksFacade drinksFacade;

	@Autowired
	private SecurityService securityService;

	@Resource(name = "drinksPageFactory")
	private PageFactory pageFactory;

	private GrassRequest request;

	private Grid<BeerOverviewTO> grid;
	private Embedded image;
	private VerticalLayout dataLayout;

	private BeerTO choosenDrink;
	private BeerOverviewTO filterTO;

	public BeersTab(GrassRequest request) {
		SpringContextHelper.inject(this);
		setMargin(new MarginInfo(true, false, false, false));

		this.request = request;

		filterTO = new BeerOverviewTO();

		grid = new Grid<>();
		Column<BeerOverviewTO, String> breweryColumn = grid.addColumn(BeerOverviewTO::getBrewery).setCaption("Pivovar");
		Column<BeerOverviewTO, String> nameColumn = grid.addColumn(BeerOverviewTO::getName).setCaption("Název");
		Column<BeerOverviewTO, String> categoryColumn = grid.addColumn(BeerOverviewTO::getCategory)
				.setCaption("Kategorie").setWidth(80);
		Column<BeerOverviewTO, Integer> degreesColumn = grid.addColumn(BeerOverviewTO::getDegrees)
				.setCaption("Stupně (°)").setWidth(80);
		Column<BeerOverviewTO, Double> alcoholColumn = grid.addColumn(BeerOverviewTO::getAlcohol)
				.setRenderer(new NumberRenderer(NumberFormat.getNumberInstance(new Locale("cs", "CZ"))))
				.setCaption("Alkohol (%)").setWidth(80);
		Column<BeerOverviewTO, Integer> ibuColumn = grid.addColumn(BeerOverviewTO::getIbu).setCaption("Hořkost (IBU)")
				.setWidth(90);
		Column<BeerOverviewTO, MaltType> maltTypeColumn = grid.addColumn(BeerOverviewTO::getMaltType)
				.setRenderer(MaltType::getCaption, new TextRenderer()).setCaption("Typ sladu").setWidth(100);
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

		// Pivovar
		TextField breweryColumnField = new TextField();
		breweryColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		breweryColumnField.setWidth("100%");
		breweryColumnField.addValueChangeListener(e -> {
			filterTO.setBrewery(e.getValue());
			populate();
		});
		filteringHeader.getCell(breweryColumn).setComponent(breweryColumnField);

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			populate();
		});
		filteringHeader.getCell(nameColumn).setComponent(nazevColumnField);

		// Kategorie
		TextField categoryColumnField = new TextField();
		categoryColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		categoryColumnField.setWidth("100%");
		categoryColumnField.addValueChangeListener(e -> {
			filterTO.setCategory(e.getValue());
			populate();
		});
		filteringHeader.getCell(categoryColumn).setComponent(categoryColumnField);

		// Stupně
		TextField degreesColumnField = new TextField();
		degreesColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		degreesColumnField.setWidth("100%");
		degreesColumnField.addValueChangeListener(e -> {
			try {
				filterTO.setDegrees(Integer.parseInt(e.getValue()));
			} catch (Exception ex) {
			}
			populate();
		});
		filteringHeader.getCell(degreesColumn).setComponent(degreesColumnField);

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

		// Hořkost
		TextField ibuColumnField = new TextField();
		ibuColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		ibuColumnField.setWidth("100%");
		ibuColumnField.addValueChangeListener(e -> {
			try {
				filterTO.setIbu(Integer.parseInt(e.getValue()));
			} catch (Exception ex) {
			}
			populate();
		});
		filteringHeader.getCell(ibuColumn).setComponent(ibuColumnField);

		// Typ sladu
		ComboBox<MaltType> typeColumnField = new ComboBox<>(null, Arrays.asList(MaltType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addStyleName(ValoTheme.COMBOBOX_TINY);
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setMaltType(e.getValue());
			populate();
		});
		typeColumnField.setItemCaptionGenerator(MaltType::getCaption);
		filteringHeader.getCell(maltTypeColumn).setComponent(typeColumnField);

		populate();

		grid.addSelectionListener((e) -> {
			if (e.getFirstSelectedItem().isPresent())
				showDetail(drinksFacade.getBeerById(e.getFirstSelectedItem().get().getId()));
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
			UI.getCurrent().addWindow(new BeerWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(BeerTO to) {
					to = drinksFacade.saveBeer(to);
					showDetail(to);
					populate();
				}
			});
		}));

		btnLayout.addComponent(new ModifyGridButton<BeerOverviewTO>("Upravit", event -> {
			UI.getCurrent().addWindow(new BeerWindow(choosenDrink) {
				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(BeerTO to) {
					to = drinksFacade.saveBeer(to);
					showDetail(to);
					populate();
				}
			});
		}, grid));

		btnLayout.addComponent(new DeleteGridButton<BeerOverviewTO>("Smazat", items -> {
			for (BeerOverviewTO s : items)
				drinksFacade.deleteDrink(s.getId());
			populate();
			showDetail(null);
		}, grid));

	}

	public void selectDrink(Long id) {
		BeerOverviewTO to = new BeerOverviewTO();
		to.setId(id);
		grid.select(to);
	}

	private void showDetail(BeerTO choosenDrink) {
		this.choosenDrink = choosenDrink;
		dataLayout.removeAllComponents();
		if (choosenDrink == null) {
			image.setVisible(false);
			String currentURL = request.getContextRoot() + "/" + pageFactory.getPageName();
			Page.getCurrent().pushState(currentURL);
		} else {
			byte[] co = choosenDrink.getImage();
			if (co != null) {
				// https://vaadin.com/forum/thread/260778
				String name = choosenDrink.getName()
						+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				image.setVisible(true);
				image.setSource(new StreamResource(() -> new ByteArrayInputStream(co), name));
				image.markAsDirty();
			} else {
				image.setVisible(false);
			}

			H2Label nameLabel = new H2Label(
					choosenDrink.getBrewery() + " " + choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")");
			dataLayout.addComponent(nameLabel);

			RatingStars rs = new RatingStars();
			rs.setValue(choosenDrink.getRating());
			rs.setReadOnly(true);
			dataLayout.addComponent(rs);

			GridLayout infoLayout = new GridLayout(2, 7);
			dataLayout.addComponent(infoLayout);

			BoldLabel b;
			infoLayout.addComponent(b = new BoldLabel("Kategorie"));
			b.setWidth("120px");
			infoLayout.addComponent(new Label(choosenDrink.getCategory()));
			infoLayout.addComponent(new BoldLabel("Stupně (°)"));
			infoLayout.addComponent(new Label(String.valueOf(choosenDrink.getDegrees())));
			infoLayout.addComponent(new BoldLabel("Alkohol (%)"));
			infoLayout.addComponent(new Label(String.valueOf(choosenDrink.getAlcohol())));
			infoLayout.addComponent(new BoldLabel("Hořkost (IBU)"));
			infoLayout.addComponent(
					new Label(choosenDrink.getIbu() == null ? "" : String.valueOf(choosenDrink.getIbu())));
			infoLayout.addComponent(new BoldLabel("Typ sladu"));
			infoLayout.addComponent(new Label(choosenDrink.getMaltType().getCaption()));
			infoLayout.addComponent(new BoldLabel("Slady"));
			infoLayout.addComponent(new Label(choosenDrink.getMalts()));
			infoLayout.addComponent(new BoldLabel("Chmely"));
			infoLayout.addComponent(new Label(choosenDrink.getHops()));

			Label descriptionLabel = new Label(choosenDrink.getDescription());
			dataLayout.addComponent(descriptionLabel);

			String currentURL;
			try {
				currentURL = request.getContextRoot() + "/" + pageFactory.getPageName() + "/beer/"
						+ choosenDrink.getId() + "-" + URLEncoder.encode(choosenDrink.getName(), "UTF-8");
				Page.getCurrent().pushState(currentURL);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

	}

	private void populate() {
		grid.setDataProvider(
				(sortOrder, offset, limit) -> drinksFacade.getBeers(filterTO, offset, limit, sortOrder).stream(),
				() -> drinksFacade.countBeers(filterTO));
	}

}
