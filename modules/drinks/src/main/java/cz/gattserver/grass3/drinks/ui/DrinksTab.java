package cz.gattserver.grass3.drinks.ui;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import cz.gattserver.grass3.ui.util.RatingStars;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;

public abstract class DrinksTab<T extends DrinkTO, O extends DrinkOverviewTO> extends VerticalLayout {

	private static final long serialVersionUID = 594189301140808163L;

	private transient SecurityService securityService;
	private transient PageFactory drinksPageFactory;
	private transient DrinksFacade drinksFacade;

	private Image image;
	private VerticalLayout dataLayout;

	protected Grid<O> grid;
	protected O filterTO;
	protected T choosenDrink;

	public DrinksTab() {
		SpringContextHelper.inject(this);

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
		contentLayout.setPadding(true);

		Div panel = new Div(contentLayout);
		panel.setWidth("100%");
		panel.setHeight("100%");
		add(panel);

		// musí tady něco být nahrané, jinak to pak nejde měnit (WTF?!)
		image = new Image(ImageIcon.BUBBLE_16_ICON.createResource(), "icon");
		image.setVisible(false);
		contentLayout.add(image);
		contentLayout.setVerticalComponentAlignment(Alignment.START, image);

		dataLayout = new VerticalLayout();
		dataLayout.setWidth("100%");
		dataLayout.setPadding(false);
		contentLayout.add(dataLayout);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		add(btnLayout);

		btnLayout.setVisible(getSecurityService().getCurrentUser().getRoles().contains(CoreRole.ADMIN));

		populateBtnLayout(btnLayout);
	}

	protected void addNameColumn(Grid<O> grid, HeaderRow filteringHeader) {
		// Název
		Column<O> nameColumn = grid.addColumn(O::getName).setHeader("Název").setSortProperty("name");
		TextField nazevColumnField = new TextField();
		nazevColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			populate();
		});
		filteringHeader.getCell(nameColumn).setComponent(nazevColumnField);
	}

	protected void addCountryColumn(Grid<O> grid, HeaderRow filteringHeader) {
		// Země původu
		Column<O> countryColumn = grid.addColumn(O::getCountry).setHeader("Země").setSortProperty("country");
		TextField countryColumnField = new TextField();
		countryColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		countryColumnField.setWidth("100%");
		countryColumnField.addValueChangeListener(e -> {
			filterTO.setCountry(e.getValue());
			populate();
		});
		filteringHeader.getCell(countryColumn).setComponent(countryColumnField);
	}

	protected void addAlcoholColumn(Grid<O> grid, HeaderRow filteringHeader) {
		// Alkohol
		Column<O> alcoholColumn = grid.addColumn(
				new NumberRenderer<O>(O::getAlcohol, NumberFormat.getNumberInstance(new Locale("cs", "CZ")), null))
				.setHeader("Alkohol (%)").setWidth("80px").setFlexGrow(0).setSortProperty("alcohol");
		TextField alcoholColumnField = new TextField();
		alcoholColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		alcoholColumnField.setWidth("100%");
		alcoholColumnField.addValueChangeListener(e -> {
			filterTO.setAlcohol(Double.parseDouble(e.getValue()));
			populate();
		});
		filteringHeader.getCell(alcoholColumn).setComponent(alcoholColumnField);
	}

	protected void addRatingStarsColumn(Grid<O> grid) {
		grid.addColumn(new ComponentRenderer<RatingStars, O>(to -> {
			RatingStars rs = new RatingStars();
			rs.setValue(to.getRating());
			rs.setReadOnly(true);
			rs.setSize("15px");
			return rs;
		})).setHeader("Hodnocení").setAutoWidth(true).setSortProperty("rating");
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

	protected abstract Grid<O> createGrid(O filterTO);

	protected abstract void populate();

	protected abstract void populateBtnLayout(HorizontalLayout btnLayout);

	protected abstract void populateDetail(VerticalLayout dataLayout);

	protected abstract String getURLPath();

	protected abstract T findById(Long id);

}
