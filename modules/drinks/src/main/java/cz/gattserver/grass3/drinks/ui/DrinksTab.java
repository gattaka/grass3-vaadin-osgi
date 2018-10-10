package cz.gattserver.grass3.drinks.ui;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.teemu.ratingstars.RatingStars;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.drinks.facades.DrinksFacade;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkTO;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;

public abstract class DrinksTab<T extends DrinkTO, O extends DrinkOverviewTO> extends VerticalLayout {

	private static final long serialVersionUID = 594189301140808163L;

	private static Logger logger = LoggerFactory.getLogger(DrinksTab.class);

	private transient SecurityService securityService;
	private transient PageFactory drinksPageFactory;
	private transient DrinksFacade drinksFacade;

	private GrassRequest request;
	private Embedded image;
	private VerticalLayout dataLayout;

	protected Grid<O> grid;
	protected O filterTO;
	protected T choosenDrink;

	public DrinksTab(GrassRequest request) {
		SpringContextHelper.inject(this);
		setMargin(new MarginInfo(true, false, false, false));

		this.request = request;

		filterTO = createNewOverviewTO();
		grid = createGrid(filterTO);

		populate();

		grid.addSelectionListener(e -> {
			if (e.getFirstSelectedItem().isPresent())
				showDetail(findById(e.getFirstSelectedItem().get().getId()));
			else
				showDetail(null);
		});

		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.setSizeFull();
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

		btnLayout.setVisible(getSecurityService().getCurrentUser().getRoles().contains(CoreRole.ADMIN));

		populateBtnLayout(btnLayout);
	}

	protected void addNameColumn(Grid<O> grid, HeaderRow filteringHeader) {
		// Název
		Column<O, String> nameColumn = grid.addColumn(O::getName).setCaption("Název").setSortProperty("name");
		TextField nazevColumnField = new TextField();
		nazevColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			populate();
		});
		filteringHeader.getCell(nameColumn).setComponent(nazevColumnField);
	}

	protected void addCountryColumn(Grid<O> grid, HeaderRow filteringHeader) {
		// Země původu
		Column<O, String> countryColumn = grid.addColumn(O::getCountry).setCaption("Země").setSortProperty("country");
		TextField countryColumnField = new TextField();
		countryColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		countryColumnField.setWidth("100%");
		countryColumnField.addValueChangeListener(e -> {
			filterTO.setCountry(e.getValue());
			populate();
		});
		filteringHeader.getCell(countryColumn).setComponent(countryColumnField);
	}

	protected void addAlcoholColumn(Grid<O> grid, HeaderRow filteringHeader) {
		// Alkohol
		Column<O, Double> alcoholColumn = grid.addColumn(O::getAlcohol)
				.setRenderer(new NumberRenderer(NumberFormat.getNumberInstance(new Locale("cs", "CZ"))))
				.setCaption("Alkohol (%)").setWidth(80).setSortProperty("alcohol");
		TextField alcoholColumnField = new TextField();
		alcoholColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		alcoholColumnField.setWidth("100%");
		alcoholColumnField.addValueChangeListener(e -> {
			filterTO.setAlcohol(Double.parseDouble(e.getValue()));
			populate();
		});
		filteringHeader.getCell(alcoholColumn).setComponent(alcoholColumnField);
	}

	protected void addRatingStarsColumn(Grid<O> grid) {
		grid.addColumn(to -> {
			RatingStars rs = new RatingStars();
			rs.setValue(to.getRating());
			rs.setReadOnly(true);
			rs.setAnimated(false);
			return rs;
		}).setRenderer(new ComponentRenderer()).setCaption("Hodnocení").setWidth(120).setSortProperty("rating");
	}

	protected SecurityService getSecurityService() {
		if (securityService == null)
			securityService = SpringContextHelper.getBean(SecurityService.class);
		return securityService;
	}

	protected PageFactory getDrinksPageFactory() {
		if (drinksPageFactory == null)
			drinksPageFactory = (PageFactory) SpringContextHelper.getBean("drinksPageFactory");
		return drinksPageFactory;
	}

	protected DrinksFacade getDrinksFacade() {
		if (drinksFacade == null)
			drinksFacade = SpringContextHelper.getBean(DrinksFacade.class);
		return drinksFacade;
	}

	public void selectDrink(Long id) {
		O to = createNewOverviewTO();
		to.setId(id);
		grid.select(to);
	}

	protected void showDetail(T choosenDrink) {
		this.choosenDrink = choosenDrink;
		dataLayout.removeAllComponents();
		if (choosenDrink == null) {
			image.setVisible(false);
			String currentURL = request.getContextRoot() + "/" + getDrinksPageFactory().getPageName();
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

			populateDetail(dataLayout);

			String currentURL;
			try {
				currentURL = request.getContextRoot() + "/" + getDrinksPageFactory().getPageName() + "/" + getURLPath()
						+ "/" + choosenDrink.getId() + "-" + URLEncoder.encode(choosenDrink.getName(), "UTF-8");
				Page.getCurrent().pushState(currentURL);
			} catch (UnsupportedEncodingException e) {
				logger.error("UnsupportedEncodingException in URL", e);
			}
		}

	}

	protected abstract O createNewOverviewTO();

	protected abstract Grid<O> createGrid(O filterTO);

	protected abstract void populate();

	protected abstract void populateBtnLayout(HorizontalLayout btnLayout);

	protected abstract void populateDetail(VerticalLayout dataLayout);

	protected abstract String getURLPath();

	protected abstract T findById(Long id);

}
