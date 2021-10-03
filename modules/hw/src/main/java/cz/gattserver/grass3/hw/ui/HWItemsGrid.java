package cz.gattserver.grass3.hw.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;

import cz.gattserver.grass3.hw.interfaces.HWFilterTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass3.hw.interfaces.HWItemState;
import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.model.util.QuerydslUtil;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.util.TokenField;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.FieldUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.LinkButton;

public class HWItemsGrid extends Div {

	private static final long serialVersionUID = -6094970451516214174L;

	private static final String NAME_BIND = "nameBind";
	private static final String USED_IN_BIND = "usedInBind";
	private static final String SUPERVIZED_FOR_BIND = "supervizedForBind";
	private static final String PRICE_BIND = "priceBind";
	private static final String STATE_BIND = "stateBind";
	private static final String PURCHASE_DATE_BIND = "purchaseDateBind";

	@Autowired
	private HWService hwService;

	@Autowired
	private SecurityService securityFacade;

	private Grid<HWItemOverviewTO> grid;
	private TokenField hwTypesFilter;

	private Map<String, HWItemTypeTO> tokenMap = new HashMap<String, HWItemTypeTO>();
	private HWFilterTO filterTO;

	public HWItemsGrid(Consumer<HWItemOverviewTO> onSelect) {
		SpringContextHelper.inject(this);

		filterTO = new HWFilterTO();
		if (!securityFacade.getCurrentUser().isAdmin())
			filterTO.setPublicItem(true);

		if (securityFacade.getCurrentUser().isAdmin()) {
			// Filtr na typy HW
			for (HWItemTypeTO type : hwService.getAllHWTypes())
				tokenMap.put(type.getName(), type);

			hwTypesFilter = new TokenField(tokenMap.keySet());
			hwTypesFilter.getInputField().setWidth("200px");
			hwTypesFilter.addTokenAddListener(token -> populate());
			hwTypesFilter.addTokenRemoveListener(e -> populate());
			hwTypesFilter.setAllowNewItems(false);
			hwTypesFilter.getInputField().setPlaceholder("Filtrovat dle typu hw");
			add(hwTypesFilter);
		}

		// Tabulka HW
		grid = new Grid<>();
		grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		UIUtils.applyGrassDefaultStyle(grid);
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setWidthFull();
		grid.setHeight("480px");

		grid.addColumn(new IconRenderer<HWItemOverviewTO>(c -> {
			ImageIcon ii = HWUIUtils.chooseImageIcon(c);
			if (ii != null) {
				Image img = new Image(ii.createResource(), c.getState().getName());
				img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
				img.setTitle(c.getState().getName());
				return img;
			} else {
				return new Span();
			}
		}, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER);

		Column<HWItemOverviewTO> nameColumn = grid
				.addColumn(new ComponentRenderer<Button, HWItemOverviewTO>(
						to -> new LinkButton(to.getName(), e -> onSelect.accept(to))))
				.setHeader("Název").setSortable(true).setKey(NAME_BIND).setResizable(true);
		// kontrola na null je tady jenom proto, aby při selectu (kdy se udělá
		// nový objekt a dá se mu akorát ID, které se porovnává) aplikace
		// nespadla na NPE -- což je trochu zvláštní, protože ve skutečnosti
		// žádný majetek nemá stav null.
		Column<HWItemOverviewTO> stateColumn = grid
				.addColumn(hw -> hw.getState() == null ? "" : hw.getState().getName()).setHeader("Stav")
				.setKey(STATE_BIND).setWidth("120px").setFlexGrow(0).setSortable(true);
		Column<HWItemOverviewTO> usedInColumn = grid.addColumn(HWItemOverviewTO::getUsedInName).setKey(USED_IN_BIND)
				.setHeader("Je součástí").setWidth("120px").setFlexGrow(0).setSortable(true);
		Column<HWItemOverviewTO> supervizedColumn = grid.addColumn(HWItemOverviewTO::getSupervizedFor)
				.setKey(SUPERVIZED_FOR_BIND).setHeader("Spravováno pro").setWidth("110px").setFlexGrow(0)
				.setSortable(true);
		if (securityFacade.getCurrentUser().isAdmin()) {
			grid.addColumn(hw -> FieldUtils.formatMoney(hw.getPrice())).setHeader("Cena").setKey(PRICE_BIND)
					.setTextAlign(ColumnTextAlign.END).setWidth("90px").setFlexGrow(0).setSortable(true);
		}
		grid.addColumn(new LocalDateRenderer<HWItemOverviewTO>(HWItemOverviewTO::getPurchaseDate, "d.M.yyyy"))
				.setHeader("Získáno").setKey(PURCHASE_DATE_BIND).setTextAlign(ColumnTextAlign.END).setWidth("80px")
				.setFlexGrow(0).setSortable(true);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		UIUtils.addHeaderTextField(filteringHeader.getCell(nameColumn), e -> {
			filterTO.setName(e.getValue());
			populate();
		});

		// Stav
		UIUtils.addHeaderComboBox(filteringHeader.getCell(stateColumn), HWItemState.class, HWItemState::getName, e -> {
			filterTO.setState(e.getValue());
			populate();
		});

		// Je součástí
		UIUtils.addHeaderTextField(filteringHeader.getCell(usedInColumn), e -> {
			filterTO.setUsedIn(e.getValue());
			populate();
		});

		// Spravován pro
		UIUtils.addHeaderTextField(filteringHeader.getCell(supervizedColumn), e -> {
			filterTO.setSupervizedFor(e.getValue());
			populate();
		});

		populate();
		grid.sort(Arrays.asList(new GridSortOrder<>(nameColumn, SortDirection.ASCENDING)));

		add(grid);
	}

	public void populate() {
		if (hwTypesFilter != null)
			filterTO.setTypes(hwTypesFilter.getValues());
		if (grid.getDataProvider() == null || !(grid.getDataProvider() instanceof CallbackDataProvider)) {
			FetchCallback<HWItemOverviewTO, HWItemOverviewTO> fetchCallback = q -> hwService.getHWItems(filterTO,
					q.getOffset(), q.getLimit(), QuerydslUtil.transformOrdering(q.getSortOrders(), column -> {
						switch (column) {
						case PRICE_BIND:
							return "price";
						case STATE_BIND:
							return "state";
						case PURCHASE_DATE_BIND:
							return "purchaseDate";
						case NAME_BIND:
							return "name";
						case USED_IN_BIND:
							return "usedIn";
						case SUPERVIZED_FOR_BIND:
							return "supervizedFor";
						default:
							return column;
						}
					})).stream();
			CountCallback<HWItemOverviewTO, HWItemOverviewTO> countCallback = q -> hwService.countHWItems(filterTO);
			grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
		} else {
			grid.getDataProvider().refreshAll();
		}
	}

	public Grid<HWItemOverviewTO> getGrid() {
		return grid;
	}

	public HWFilterTO getFilterTO() {
		return filterTO;
	}

}
