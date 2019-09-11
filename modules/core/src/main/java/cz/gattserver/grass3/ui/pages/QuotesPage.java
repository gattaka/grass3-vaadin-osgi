package cz.gattserver.grass3.ui.pages;

import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.grass3.interfaces.QuoteTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.UIUtils;

public class QuotesPage extends OneColumnPage {

	private List<QuoteTO> data;

	public QuotesPage(GrassRequest request) {
		super(request);
	}

	private void populateData() {
		data = quotesFacade.getAllQuotes();
	}

	@Override
	protected Component createColumnContent() {

		VerticalLayout layout = new VerticalLayout();

		layout.setPadding(true);
		layout.setSpacing(true);

		Grid<QuoteTO> grid = new Grid<>();
		grid.setSizeFull();
		layout.add(grid);

		populateData();

		grid.setItems(data);
		grid.addColumn(QuoteTO::getId).setHeader("Id").setFlexGrow(0).setWidth("50px");
		grid.addColumn(QuoteTO::getName).setHeader("Obsah");

		HorizontalLayout btnLayout = new HorizontalLayout();
		layout.add(btnLayout);
		layout.setHorizontalComponentAlignment(Alignment.CENTER, btnLayout);
		btnLayout.setVisible(coreACL.canModifyQuotes(UIUtils.getUser()));

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

		return layout;

	}
}
