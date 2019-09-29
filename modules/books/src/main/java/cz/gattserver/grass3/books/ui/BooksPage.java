package cz.gattserver.grass3.books.ui;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass3.books.facades.BooksFacade;
import cz.gattserver.grass3.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass3.books.model.interfaces.BookTO;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.RatingStars;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.Strong;
import cz.gattserver.web.common.ui.Breakline;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.ImageIcon;

@Route("books")
public class BooksPage extends OneColumnPage implements HasUrlParameter<String> {

	private static final long serialVersionUID = -5187973603822110627L;

	private transient SecurityService securityService;
	private transient PageFactory booksPageFactory;
	private transient BooksFacade booksFacade;

	private Image image;
	private Div dataLayout;
	private HorizontalLayout contentLayout;

	private Grid<BookOverviewTO> grid;
	private BookOverviewTO filterTO;
	private BookTO choosenBook;

	private CallbackDataProvider<BookOverviewTO, BookOverviewTO> dataProvider;

	private String parameter;

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		this.parameter = parameter;
		init();
	}

	@Override
	protected void createColumnContent(Div layout) {
		filterTO = new BookOverviewTO();
		grid = createGrid(filterTO);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
		layout.add(grid);

		grid.addSelectionListener(e -> {
			if (e.getFirstSelectedItem().isPresent())
				showDetail(findById(e.getFirstSelectedItem().get().getId()));
			else
				showDetail(null);
		});

		contentLayout = new HorizontalLayout();
		contentLayout.setSizeFull();
		contentLayout.setPadding(false);
		contentLayout.setVisible(false);
		contentLayout.addClassName("top-margin");
		layout.add(contentLayout);

		// musí tady něco být nahrané, jinak to pak nejde měnit (WTF?!)
		image = new Image(ImageIcon.BUBBLE_16_ICON.createResource(), "icon");
		image.setVisible(false);
		contentLayout.add(image);
		contentLayout.setVerticalComponentAlignment(Alignment.START, image);

		dataLayout = new Div();
		dataLayout.setWidth("100%");
		contentLayout.add(dataLayout);

		ButtonLayout btnLayout = new ButtonLayout();
		btnLayout.setVisible(getSecurityService().getCurrentUser().getRoles().contains(CoreRole.ADMIN));
		layout.add(btnLayout);

		populateBtnLayout(btnLayout);

		if (parameter != null) {
			URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(parameter);
			selectBook(identifier.getId());
		}
	}

	protected Grid<BookOverviewTO> createGrid(final BookOverviewTO filterTO) {

		final Grid<BookOverviewTO> grid = new Grid<>();
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
		grid.setWidth("100%");
		grid.setHeight("400px");

		Column<BookOverviewTO> authorColumn = grid.addColumn(BookOverviewTO::getAuthor).setHeader("Autor")
				.setFlexGrow(50).setAutoWidth(true).setSortProperty("author");
		Column<BookOverviewTO> nameColumn = grid.addColumn(BookOverviewTO::getName).setHeader("Název").setFlexGrow(50)
				.setAutoWidth(true).setSortProperty("name");
		grid.addColumn(new ComponentRenderer<RatingStars, BookOverviewTO>(to -> {
			RatingStars rs = new RatingStars();
			rs.setValue(to.getRating());
			rs.setReadOnly(true);
			rs.setSize("15px");
			return rs;
		})).setHeader("Hodnocení").setAutoWidth(true).setSortProperty("rating");

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Autor
		TextField authorColumnField = new TextField();
		authorColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		authorColumnField.setWidth("100%");
		authorColumnField.addValueChangeListener(e -> {
			filterTO.setAuthor(e.getValue());
			dataProvider.refreshAll();
		});
		filteringHeader.getCell(authorColumn).setComponent(authorColumnField);

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			dataProvider.refreshAll();
		});
		filteringHeader.getCell(nameColumn).setComponent(nazevColumnField);

		FetchCallback<BookOverviewTO, BookOverviewTO> fetchCallback = q -> getBooksFacade()
				.getBooks(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
		CountCallback<BookOverviewTO, BookOverviewTO> countCallback = q -> getBooksFacade().countBooks(filterTO);
		dataProvider = DataProvider.fromFilteringCallbacks(fetchCallback, countCallback);
		grid.setDataProvider(dataProvider);

		return grid;
	}

	protected void populateBtnLayout(ButtonLayout btnLayout) {
		btnLayout.add(new CreateGridButton("Přidat", event -> new BookDialog() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(BookTO to) {
				to = getBooksFacade().saveBook(to);
				showDetail(to);
				dataProvider.refreshAll();
			}
		}.open()));

		btnLayout.add(new ModifyGridButton<BookOverviewTO>("Upravit", event -> new BookDialog(choosenBook) {
			private static final long serialVersionUID = 5264621441522056786L;

			@Override
			protected void onSave(BookTO to) {
				to = getBooksFacade().saveBook(to);
				showDetail(to);
				dataProvider.refreshItem(to);
			}
		}.open(), grid));

		btnLayout.add(new DeleteGridButton<BookOverviewTO>("Smazat", items -> {
			for (BookOverviewTO s : items)
				getBooksFacade().deleteBook(s.getId());
			dataProvider.refreshAll();
			showDetail(null);
		}, grid));
	}

	protected void populateDetail(Div dataLayout) {
		H2 nameLabel = new H2(choosenBook.getName());
		dataLayout.add(nameLabel);

		RatingStars rs = new RatingStars();
		rs.setValue(choosenBook.getRating());
		rs.setReadOnly(true);
		dataLayout.add(rs);

		Div infoLayout = new Div();
		infoLayout.addClassName("top-margin");
		dataLayout.add(infoLayout);

		infoLayout.add(new Strong("Autor"));
		infoLayout.add(new Breakline());
		infoLayout.add(choosenBook.getAuthor());
		infoLayout.add(new Breakline());
		infoLayout.add(new Breakline());
		infoLayout.add(new Strong("Vydáno"));
		infoLayout.add(new Breakline());
		infoLayout.add(choosenBook.getYear());
		infoLayout.add(new Breakline());
		infoLayout.add(new Breakline());

		HtmlDiv description = new HtmlDiv(choosenBook.getDescription().replaceAll("\n", "<br/>"));
		description.setSizeFull();
		dataLayout.add(description);
	}

	protected SecurityService getSecurityService() {
		if (securityService == null)
			securityService = SpringContextHelper.getBean(SecurityService.class);
		return securityService;
	}

	protected PageFactory getBooksPageFactory() {
		if (booksPageFactory == null)
			booksPageFactory = (PageFactory) SpringContextHelper.getBean("booksPageFactory");
		return booksPageFactory;
	}

	protected BooksFacade getBooksFacade() {
		if (booksFacade == null)
			booksFacade = SpringContextHelper.getBean(BooksFacade.class);
		return booksFacade;
	}

	public void selectBook(Long id) {
		BookOverviewTO to = new BookOverviewTO();
		to.setId(id);
		grid.select(to);
	}

	protected void showDetail(BookTO choosenBook) {
		this.choosenBook = choosenBook;
		dataLayout.removeAll();
		if (choosenBook == null) {
			contentLayout.setVisible(false);
			// TODO
			// String currentURL = request.getContextRoot() + "/" +
			// getBooksPageFactory().getPageName();
			// UI.getCurrent().getRouter().
			// Page.getCurrent().pushState(currentURL);
		} else {
			contentLayout.setVisible(true);
			byte[] co = choosenBook.getImage();
			if (co != null) {
				// https://vaadin.com/forum/thread/260778
				String name = choosenBook.getName()
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
			// getBooksPageFactory().getPageName() + "/"
			// + +choosenBook.getId() + "-" +
			// URLEncoder.encode(choosenBook.getName(), "UTF-8");
			// Page.getCurrent().pushState(currentURL);
			// } catch (UnsupportedEncodingException e) {
			// logger.error("UnsupportedEncodingException in URL", e);
			// }
		}
	}

	protected BookTO findById(Long id) {
		return getBooksFacade().getBookById(id);
	}
}
