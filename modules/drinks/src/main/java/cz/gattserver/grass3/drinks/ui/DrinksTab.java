package cz.gattserver.grass3.drinks.ui;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass3.drinks.facades.DrinksFacade;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkTO;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.RatingStars;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;

public abstract class DrinksTab<T extends DrinkTO, O extends DrinkOverviewTO> extends Div {

	private static final long serialVersionUID = 594189301140808163L;

	private transient SecurityService securityService;
	private transient PageFactory drinksPageFactory;
	private transient DrinksFacade drinksFacade;

	private Image image;
	private Div dataLayout;

	protected Grid<O> grid;
	protected O filterTO;
	protected T choosenDrink;

	private HeaderRow filteringHeader;

	public DrinksTab() {
		SpringContextHelper.inject(this);

		filterTO = createNewOverviewTO();
		grid = new Grid<>();
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
		grid.addClassName("top-margin");
		configureGrid(grid, filterTO);

		populate();

		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.setSizeFull();
		contentLayout.setPadding(false);
		contentLayout.setVisible(false);
		contentLayout.addClassName("top-margin");
		contentLayout.getStyle().set("border", "1px #dbdee4 solid").set("padding", "10px").set("background", "white");
		add(contentLayout);

		grid.addSelectionListener(e -> {
			if (e.getFirstSelectedItem().isPresent()) {
				showDetail(findById(e.getFirstSelectedItem().get().getId()));
				contentLayout.setVisible(true);
			} else {
				showDetail(null);
				contentLayout.setVisible(false);
			}
		});

		// musí tady něco být nahrané, jinak to pak nejde měnit (WTF?!)
		image = new Image(ImageIcon.BUBBLE_16_ICON.createResource(), "icon");
		image.setVisible(false);
		contentLayout.add(image);

		dataLayout = new Div();
		dataLayout.setWidthFull();
		contentLayout.add(dataLayout);

		if (getSecurityService().getCurrentUser().getRoles().contains(CoreRole.ADMIN)) {
			ButtonLayout btnLayout = new ButtonLayout();
			add(btnLayout);
			populateBtnLayout(btnLayout);
		}
	}

	protected HeaderRow getHeaderRow() {
		if (filteringHeader == null)
			filteringHeader = grid.appendHeaderRow();
		return filteringHeader;
	}

	protected void addNameColumn(Grid<O> grid) {
		// Název
		Column<O> nameColumn = grid.addColumn(O::getName).setHeader("Název").setSortProperty("name").setFlexGrow(100);
		TextField nazevColumnField = new TextField();
		nazevColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			populate();
		});
		getHeaderRow().getCell(nameColumn).setComponent(nazevColumnField);
	}

	protected void addCountryColumn(Grid<O> grid) {
		// Země původu
		Column<O> countryColumn = grid.addColumn(O::getCountry).setHeader("Země").setSortProperty("country");
		TextField countryColumnField = new TextField();
		countryColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		countryColumnField.setWidth("100%");
		countryColumnField.addValueChangeListener(e -> {
			filterTO.setCountry(e.getValue());
			populate();
		});
		getHeaderRow().getCell(countryColumn).setComponent(countryColumnField);
	}

	protected void addAlcoholColumn(Grid<O> grid) {
		Column<O> alcoholColumn = grid.addColumn(
				new NumberRenderer<O>(O::getAlcohol, NumberFormat.getNumberInstance(new Locale("cs", "CZ")), null))
				.setHeader("Alkohol (%)").setWidth("100px").setFlexGrow(0).setSortProperty("alcohol");
		TextField alcoholColumnField = new TextField();
		alcoholColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		alcoholColumnField.setWidth("100%");
		alcoholColumnField.addValueChangeListener(e -> {
			filterTO.setAlcohol(Double.parseDouble(e.getValue()));
			populate();
		});
		getHeaderRow().getCell(alcoholColumn).setComponent(alcoholColumnField);
	}

	protected void addRatingStarsColumn(Grid<O> grid) {
		grid.addColumn(new ComponentRenderer<RatingStars, O>(to -> {
			RatingStars rs = new RatingStars();
			rs.setValue(to.getRating());
			rs.setReadOnly(true);
			rs.setSize("15px");
			return rs;
		})).setHeader("Hodnocení").setWidth("90px").setFlexGrow(0).setSortProperty("rating");
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
		dataLayout.removeAll();
		if (choosenDrink == null) {
			image.setVisible(false);
			// TODO
			// String currentURL = request.getContextRoot() + "/" +
			// getDrinksPageFactory().getPageName();
			// Page.getCurrent().pushState(currentURL);
		} else {
			byte[] co = choosenDrink.getImage();
			if (co != null) {
				// https://vaadin.com/forum/thread/260778
				String name = choosenDrink.getName()
						+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				image.setVisible(true);
				image.setSrc(new StreamResource(name, () -> new ByteArrayInputStream(co)));
			} else {
				image.setVisible(false);
			}

			populateDetail(dataLayout);

			// TODO
			// String currentURL;
			// try {
			// currentURL = request.getContextRoot() + "/" +
			// getDrinksPageFactory().getPageName() + "/" + getURLPath()
			// + "/" + choosenDrink.getId() + "-" +
			// URLEncoder.encode(choosenDrink.getName(), "UTF-8");
			// Page.getCurrent().pushState(currentURL);
			// } catch (UnsupportedEncodingException e) {
			// logger.error("UnsupportedEncodingException in URL", e);
			// }
		}

	}

	protected abstract O createNewOverviewTO();

	protected abstract void configureGrid(Grid<O> grid, O filterTO);

	protected abstract void populate();

	protected abstract void populateBtnLayout(ButtonLayout btnLayout);

	protected abstract void populateDetail(Div dataLayout);

	protected abstract String getURLPath();

	protected abstract T findById(Long id);

}
