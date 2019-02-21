package cz.gattserver.grass3.books.ui;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.teemu.ratingstars.RatingStars;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
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
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.books.facades.BooksFacade;
import cz.gattserver.grass3.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass3.books.model.interfaces.BookTO;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.BoldLabel;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.ui.ImageIcon;

public class BooksPage extends OneColumnPage {

	private static Logger logger = LoggerFactory.getLogger(BooksPage.class);

	private transient SecurityService securityService;
	private transient PageFactory booksPageFactory;
	private transient BooksFacade booksFacade;

	private GrassRequest request;
	private Embedded image;
	private VerticalLayout dataLayout;

	protected Grid<BookOverviewTO> grid;
	protected BookOverviewTO filterTO;
	protected BookTO choosenBook;

	public BooksPage(GrassRequest request) {
		super(request);
		this.request = request;
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		VerticalLayout wrapperLayout = new VerticalLayout();
		layout.setMargin(true);
		wrapperLayout.addComponent(layout);

		filterTO = new BookOverviewTO();
		grid = createGrid(filterTO);
		layout.addComponent(grid);

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
		layout.addComponent(panel);
		layout.setExpandRatio(panel, 1);

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
		layout.addComponent(btnLayout);

		btnLayout.setVisible(getSecurityService().getCurrentUser().getRoles().contains(CoreRole.ADMIN));

		populateBtnLayout(btnLayout);

		String token = getRequest().getAnalyzer().getNextPathToken();
		if (token != null) {
			URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(token);
			selectBook(identifier.getId());
		}

		return wrapperLayout;
	}

	protected Grid<BookOverviewTO> createGrid(final BookOverviewTO filterTO) {

		final Grid<BookOverviewTO> grid = new Grid<>();
		grid.setWidth("100%");
		grid.setHeight("400px");

		HeaderRow filteringHeader = grid.appendHeaderRow();

		Column<BookOverviewTO, String> authorColumn = grid.addColumn(BookOverviewTO::getAuthor).setCaption("Autor")
				.setSortProperty("author");

		// Autor
		TextField authorColumnField = new TextField();
		authorColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		authorColumnField.setWidth("100%");
		authorColumnField.addValueChangeListener(e -> {
			filterTO.setAuthor(e.getValue());
			populate();
		});
		filteringHeader.getCell(authorColumn).setComponent(authorColumnField);

		// Název
		Column<BookOverviewTO, String> nameColumn = grid.addColumn(BookOverviewTO::getName).setCaption("Název")
				.setSortProperty("name");
		TextField nazevColumnField = new TextField();
		nazevColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			populate();
		});
		filteringHeader.getCell(nameColumn).setComponent(nazevColumnField);

		// Hodnocení
		grid.addColumn(to -> {
			RatingStars rs = new RatingStars();
			rs.setValue(to.getRating());
			rs.setReadOnly(true);
			rs.setAnimated(false);
			return rs;
		}).setRenderer(new ComponentRenderer()).setCaption("Hodnocení").setWidth(120).setSortProperty("rating");

		return grid;
	}

	protected void populate() {
		grid.setDataProvider(
				(sortOrder, offset, limit) -> getBooksFacade().getBooks(filterTO, offset, limit, sortOrder).stream(),
				() -> getBooksFacade().countBooks(filterTO));
	}

	protected void populateBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.addComponent(new CreateGridButton("Přidat", event -> UI.getCurrent().addWindow(new BookWindow() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(BookTO to) {
				to = getBooksFacade().saveBook(to);
				showDetail(to);
				populate();
			}
		})));

		btnLayout.addComponent(new ModifyGridButton<BookOverviewTO>("Upravit",
				event -> UI.getCurrent().addWindow(new BookWindow(choosenBook) {
					private static final long serialVersionUID = 5264621441522056786L;

					@Override
					protected void onSave(BookTO to) {
						to = getBooksFacade().saveBook(to);
						showDetail(to);
						populate();
					}
				}), grid));

		btnLayout.addComponent(new DeleteGridButton<BookOverviewTO>("Smazat", items -> {
			for (BookOverviewTO s : items)
				getBooksFacade().deleteBook(s.getId());
			populate();
			showDetail(null);
		}, grid));
	}

	protected void populateDetail(VerticalLayout dataLayout) {
		H2Label nameLabel = new H2Label(choosenBook.getName());
		dataLayout.addComponent(nameLabel);

		RatingStars rs = new RatingStars();
		rs.setValue(choosenBook.getRating());
		rs.setReadOnly(true);
		rs.setAnimated(false);
		dataLayout.addComponent(rs);

		GridLayout infoLayout = new GridLayout(2, 7);
		dataLayout.addComponent(infoLayout);

		BoldLabel b = new BoldLabel("Autor");
		infoLayout.addComponent(b);
		b.setWidth("120px");
		infoLayout.addComponent(new Label(choosenBook.getAuthor()));
		infoLayout.addComponent(new BoldLabel("Vydáno"));
		infoLayout.addComponent(new Label(choosenBook.getReleased() == null ? ""
				: choosenBook.getReleased().format(DateTimeFormatter.ofPattern("d.M.yyyy"))));

		Label descriptionLabel = new Label(choosenBook.getDescription().replaceAll("\n", "<br/>"), ContentMode.HTML);
		descriptionLabel.setSizeFull();
		dataLayout.addComponent(descriptionLabel);
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
		dataLayout.removeAllComponents();
		if (choosenBook == null) {
			image.setVisible(false);
			String currentURL = request.getContextRoot() + "/" + getBooksPageFactory().getPageName();
			Page.getCurrent().pushState(currentURL);
		} else {
			byte[] co = choosenBook.getImage();
			if (co != null) {
				// https://vaadin.com/forum/thread/260778
				String name = choosenBook.getName()
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
				currentURL = request.getContextRoot() + "/" + getBooksPageFactory().getPageName() + "/"
						+ +choosenBook.getId() + "-" + URLEncoder.encode(choosenBook.getName(), "UTF-8");
				Page.getCurrent().pushState(currentURL);
			} catch (UnsupportedEncodingException e) {
				logger.error("UnsupportedEncodingException in URL", e);
			}
		}
	}

	protected BookTO findById(Long id) {
		return getBooksFacade().getBookById(id);
	}
}
