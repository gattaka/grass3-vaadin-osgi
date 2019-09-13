package cz.gattserver.grass3.ui.pages;

import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.grass3.interfaces.QuoteTO;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.dialogs.QuoteDialog;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;

@Route("quotes")
public class QuotesPage extends OneColumnPage {

	private static final long serialVersionUID = 6209768531464272839L;

	private List<QuoteTO> data;

	public QuotesPage() {
		init();
	}

	private void populateData() {
		data = quotesFacade.getAllQuotes();
	}

	@Override
	protected void createColumnContent(Div layout) {
		Grid<QuoteTO> grid = new Grid<>();
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
		layout.add(grid);

		populateData();

		grid.setItems(data);
		grid.addColumn(QuoteTO::getId).setHeader("Id").setFlexGrow(0).setWidth("50px");
		grid.addColumn(QuoteTO::getName).setHeader("Obsah");

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.addClassName("top-margin");
		layout.add(btnLayout);
		btnLayout.setVisible(coreACL.canModifyQuotes(getUser()));

		CreateGridButton createGridButton = new CreateGridButton("Přidat hlášku", e -> new QuoteDialog(q -> {
			quotesFacade.createQuote(q.getName());
			populateData();
			grid.setItems(data);
		}).open());
		btnLayout.add(createGridButton);

		ModifyGridButton<QuoteTO> modifyGridButton = new ModifyGridButton<>("Upravit hlášku",
				originQuote -> new QuoteDialog(originQuote, q -> {
					quotesFacade.modifyQuote(q.getId(), q.getName());
					grid.getDataProvider().refreshItem(q);
					grid.select(q);
				}).open(), grid);
		btnLayout.add(modifyGridButton);

		DeleteGridButton<QuoteTO> deleteGridButton = new DeleteGridButton<>("Odstranit hlášky",
				items -> items.forEach(q -> {
					quotesFacade.deleteQuote(q.getId());
					data.remove(q);
					grid.getDataProvider().refreshAll();
				}), grid);
		btnLayout.add(deleteGridButton);
	}
}
