package cz.gattserver.grass3.ui.pages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.interfaces.QuoteTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.CoreACLService;
import cz.gattserver.grass3.services.QuotesService;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.UIUtils;

public class QuotesPage extends OneColumnPage {

	@Autowired
	private CoreACLService coreACL;

	@Autowired
	private QuotesService quotesFacade;

	/**
	 * Seznam hlášek
	 */
	private Grid<QuoteTO> grid;

	private List<QuoteTO> data;

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

		grid = new Grid<QuoteTO>();
		grid.setSizeFull();
		layout.addComponent(grid);

		populateData();

		grid.setItems(data);
		grid.addColumn(QuoteTO::getId).setCaption("Id").setWidth(50);
		grid.addColumn(QuoteTO::getName).setCaption("Obsah");

		HorizontalLayout btnLayout = new HorizontalLayout();
		layout.addComponent(btnLayout);
		layout.setComponentAlignment(btnLayout, Alignment.MIDDLE_CENTER);
		btnLayout.setVisible(coreACL.canModifyQuotes(UIUtils.getUser()));

		CreateGridButton createGridButton = new CreateGridButton("Přidat hlášku", e -> {
			UI.getCurrent().addWindow(new QuoteWindow(q -> {
				quotesFacade.createQuote(q.getName());
				populateData();
				grid.setItems(data);
			}));
		});
		btnLayout.addComponent(createGridButton);

		ModifyGridButton<QuoteTO> modifyGridButton = new ModifyGridButton<>("Upravit hlášku", (e, originQuote) -> {
			UI.getCurrent().addWindow(new QuoteWindow(originQuote, q -> {
				quotesFacade.modifyQuote(q.getId(), q.getName());
				grid.getDataProvider().refreshItem(q);
				grid.select(q);
			}));
		}, grid);
		btnLayout.addComponent(modifyGridButton);

		DeleteGridButton<QuoteTO> deleteGridButton = new DeleteGridButton<>("Odstranit hlášky",
				items -> items.forEach(q -> {
					quotesFacade.deleteQuote(q.getId());
					data.remove(q);
					grid.getDataProvider().refreshAll();
				}), grid);
		btnLayout.addComponent(deleteGridButton);

		return layout;

	}
}
