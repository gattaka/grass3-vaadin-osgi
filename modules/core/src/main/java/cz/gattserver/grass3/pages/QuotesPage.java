package cz.gattserver.grass3.pages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.facades.QuotesFacade;
import cz.gattserver.grass3.model.dto.QuoteDTO;
import cz.gattserver.grass3.pages.template.OneColumnPage;
import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.template.CreateGridButton;
import cz.gattserver.grass3.template.DeleteGridButton;
import cz.gattserver.grass3.template.ModifyGridButton;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.window.ConfirmWindow;

public class QuotesPage extends OneColumnPage {

	private static final long serialVersionUID = 2474374292329895766L;

	@Autowired
	private CoreACL coreACL;

	@Autowired
	private QuotesFacade quotesFacade;

	/**
	 * Seznam hlášek
	 */
	private Grid<QuoteDTO> grid;

	private List<QuoteDTO> data;

	public QuotesPage(GrassRequest request) {
		super(request);
	}

	private void populateData() {
		data = quotesFacade.getAllQuotes();
	}

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		grid = new Grid<QuoteDTO>();
		grid.setSizeFull();
		layout.addComponent(grid);

		populateData();

		grid.setItems(data);
		grid.addColumn(QuoteDTO::getId).setCaption("Id").setWidth(50);
		grid.addColumn(QuoteDTO::getName).setCaption("Obsah");

		HorizontalLayout btnLayout = new HorizontalLayout();
		layout.addComponent(btnLayout);
		layout.setComponentAlignment(btnLayout, Alignment.MIDDLE_CENTER);
		btnLayout.setVisible(coreACL.canModifyQuotes(getUser()));

		CreateGridButton createGridButton = new CreateGridButton("Přidat hlášku", e -> {
			UI.getCurrent().addWindow(new QuoteWindow(q -> {
				quotesFacade.saveQuote(q);
				populateData();
				grid.setItems(data);
			}));
		});
		btnLayout.addComponent(createGridButton);

		ModifyGridButton<QuoteDTO> modifyGridButton = new ModifyGridButton<>("Upravit hlášku", (e, originQuote) -> {
			UI.getCurrent().addWindow(new QuoteWindow(originQuote, q -> {
				quotesFacade.saveQuote(q);
				grid.getDataProvider().refreshItem(q);
				grid.select(q);
			}));
		}, grid);
		btnLayout.addComponent(modifyGridButton);

		DeleteGridButton<QuoteDTO> deleteGridButton = new DeleteGridButton<>("Odstranit hlášku", q -> {
			quotesFacade.deleteQuote(q.getId());
			data.remove(q);
			grid.getDataProvider().refreshAll();
		}, grid);
		btnLayout.addComponent(deleteGridButton);

		return layout;

	}
}
