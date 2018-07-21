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
import cz.gattserver.grass3.drinks.model.domain.WhiskeyType;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyTO;
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

public class WhiskeyTab extends VerticalLayout {

	private static final long serialVersionUID = 594189301140808163L;

	@Autowired
	private DrinksFacade drinksFacade;

	@Autowired
	private SecurityService securityService;

	@Resource(name = "drinksPageFactory")
	private PageFactory pageFactory;

	private GrassRequest request;

	private Grid<WhiskeyOverviewTO> grid;
	private Embedded image;
	private VerticalLayout dataLayout;

	private WhiskeyTO choosenDrink;
	private WhiskeyOverviewTO filterTO;

	public WhiskeyTab(GrassRequest request) {
		SpringContextHelper.inject(this);
		setMargin(new MarginInfo(true, false, false, false));

		this.request = request;

		filterTO = new WhiskeyOverviewTO();

		grid = new Grid<>();
		Column<WhiskeyOverviewTO, String> nameColumn = grid.addColumn(WhiskeyOverviewTO::getName).setCaption("Název");
		Column<WhiskeyOverviewTO, String> countryColumn = grid.addColumn(WhiskeyOverviewTO::getCountry)
				.setCaption("Země");
		Column<WhiskeyOverviewTO, Double> alcoholColumn = grid.addColumn(WhiskeyOverviewTO::getAlcohol)
				.setRenderer(new NumberRenderer(NumberFormat.getNumberInstance(new Locale("cs", "CZ"))))
				.setCaption("Alkohol (%)").setWidth(80);
		Column<WhiskeyOverviewTO, Integer> yearsColumn = grid.addColumn(WhiskeyOverviewTO::getYears)
				.setCaption("Stáří (roky)").setWidth(90);
		Column<WhiskeyOverviewTO, WhiskeyType> WhiskeyTypeColumn = grid.addColumn(WhiskeyOverviewTO::getWhiskeyType)
				.setRenderer(WhiskeyType::getCaption, new TextRenderer()).setCaption("Typ whiskey").setWidth(150);
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

		// Stáří (roky)
		TextField yearsColumnField = new TextField();
		yearsColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		yearsColumnField.setWidth("100%");
		yearsColumnField.addValueChangeListener(e -> {
			try {
				filterTO.setYears(Integer.parseInt(e.getValue()));
			} catch (Exception ex) {
			}
			populate();
		});
		filteringHeader.getCell(yearsColumn).setComponent(yearsColumnField);

		// Typ Whiskeyu
		ComboBox<WhiskeyType> typeColumnField = new ComboBox<>(null, Arrays.asList(WhiskeyType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addStyleName(ValoTheme.COMBOBOX_TINY);
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setWhiskeyType(e.getValue());
			populate();
		});
		typeColumnField.setItemCaptionGenerator(WhiskeyType::getCaption);
		filteringHeader.getCell(WhiskeyTypeColumn).setComponent(typeColumnField);

		populate();

		grid.addSelectionListener((e) -> {
			if (e.getFirstSelectedItem().isPresent())
				showDetail(drinksFacade.getWhiskeyById(e.getFirstSelectedItem().get().getId()));
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
			UI.getCurrent().addWindow(new WhiskeyWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(WhiskeyTO to) {
					to = drinksFacade.saveWhiskey(to);
					showDetail(to);
					populate();
				}
			});
		}));

		btnLayout.addComponent(new ModifyGridButton<WhiskeyOverviewTO>("Upravit", event -> {
			UI.getCurrent().addWindow(new WhiskeyWindow(choosenDrink) {
				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(WhiskeyTO to) {
					to = drinksFacade.saveWhiskey(to);
					showDetail(to);
					populate();
				}
			});
		}, grid));

		btnLayout.addComponent(new DeleteGridButton<WhiskeyOverviewTO>("Smazat", items -> {
			for (WhiskeyOverviewTO s : items)
				drinksFacade.deleteDrink(s.getId());
			populate();
			showDetail(null);
		}, grid));

	}

	public void selectDrink(Long id) {
		WhiskeyOverviewTO to = new WhiskeyOverviewTO();
		to.setId(id);
		grid.select(to);
	}

	private void showDetail(WhiskeyTO choosenDrink) {
		this.choosenDrink = choosenDrink;
		dataLayout.removeAllComponents();
		if (choosenDrink == null) {
			image.setVisible(false);
			String currentURL = request.getContextRoot() + "/" + pageFactory.getPageName();
			Page.getCurrent().pushState(currentURL);
		} else {
			byte[] co = choosenDrink.getImage();
			if (co != null) {
				// https://vaadin.com/foWhiskey/thread/260778
				String name = choosenDrink.getName()
						+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				image.setVisible(true);
				image.setSource(new StreamResource(() -> new ByteArrayInputStream(co), name));
				image.markAsDirty();
			} else {
				image.setVisible(false);
			}

			H2Label nameLabel = new H2Label(choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")");
			dataLayout.addComponent(nameLabel);

			RatingStars rs = new RatingStars();
			rs.setValue(choosenDrink.getRating());
			rs.setReadOnly(true);
			dataLayout.addComponent(rs);

			GridLayout infoLayout = new GridLayout(2, 7);
			dataLayout.addComponent(infoLayout);

			BoldLabel b;
			infoLayout.addComponent(new BoldLabel("Stáří (roky)"));
			infoLayout.addComponent(new Label(String.valueOf(choosenDrink.getYears())));
			infoLayout.addComponent(b = new BoldLabel("Alkohol (%)"));
			b.setWidth("120px");
			infoLayout.addComponent(new Label(String.valueOf(choosenDrink.getAlcohol())));
			infoLayout.addComponent(new BoldLabel("Typ whiskey"));
			infoLayout.addComponent(new Label(choosenDrink.getWhiskeyType().getCaption()));

			Label descriptionLabel = new Label(choosenDrink.getDescription());
			dataLayout.addComponent(descriptionLabel);

			String currentURL;
			try {
				currentURL = request.getContextRoot() + "/" + pageFactory.getPageName() + "/whiskey/"
						+ choosenDrink.getId() + "-" + URLEncoder.encode(choosenDrink.getName(), "UTF-8");
				Page.getCurrent().pushState(currentURL);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

	}

	private void populate() {
		grid.setDataProvider(
				(sortOrder, offset, limit) -> drinksFacade.getWhiskeys(filterTO, offset, limit, sortOrder).stream(),
				() -> drinksFacade.countWhiskeys(filterTO));
	}

}
