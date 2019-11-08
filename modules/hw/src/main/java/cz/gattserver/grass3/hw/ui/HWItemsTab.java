package cz.gattserver.grass3.hw.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import cz.gattserver.grass3.hw.interfaces.HWFilterTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass3.hw.interfaces.HWItemState;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.hw.ui.dialogs.HWItemDetailsDialog;
import cz.gattserver.grass3.hw.ui.dialogs.HWItemDialog;
import cz.gattserver.grass3.model.util.QuerydslUtil;
import cz.gattserver.grass3.ui.components.button.CreateButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.GridButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.TokenField;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.FieldUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ErrorDialog;

public class HWItemsTab extends Div {

	private static final long serialVersionUID = -5013459007975657195L;

	private static final String NAME_BIND = "nameBind";
	private static final String USED_IN_BIND = "usedInBind";
	private static final String SUPERVIZED_FOR_BIND = "supervizedForBind";
	private static final String PRICE_BIND = "priceBind";
	private static final String STATE_BIND = "stateBind";
	private static final String PURCHASE_DATE_BIND = "purchaseDateBind";

	private transient HWService hwService;

	private Grid<HWItemOverviewTO> grid;
	private TokenField hwTypesFilter;

	private Map<String, HWItemTypeTO> tokenMap = new HashMap<String, HWItemTypeTO>();
	private HWFilterTO filterTO;

	public HWItemsTab() {
		filterTO = new HWFilterTO();

		// Filtr na typy HW
		for (HWItemTypeTO type : getHWService().getAllHWTypes())
			tokenMap.put(type.getName(), type);

		hwTypesFilter = new TokenField(tokenMap.keySet());
		hwTypesFilter.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		hwTypesFilter.getInputField().setWidth("200px");
		hwTypesFilter.addTokenAddListener(token -> populate());
		hwTypesFilter.addTokenRemoveListener(e -> populate());
		hwTypesFilter.setAllowNewItems(false);
		hwTypesFilter.getInputField().setPlaceholder("Filtrovat dle typu hw");
		add(hwTypesFilter);

		// Tabulka HW
		grid = new Grid<>();
		grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setWidthFull();
		grid.setHeight("480px");

		grid.addColumn(new IconRenderer<HWItemOverviewTO>(c -> {
			ImageIcon ii = HWUIUtils.chooseImageIcon(c);
			if (ii != null) {
				Image img = new Image(ii.createResource(), c.getState().getName());
				img.getStyle().set("margin-bottom", "-4px");
				return img;
			} else {
				return new Span();
			}
		}, c -> "")).setFlexGrow(0).setWidth("26px").setHeader("");

		Column<HWItemOverviewTO> nameColumn = grid.addColumn(HWItemOverviewTO::getName).setKey(NAME_BIND)
				.setHeader("Název").setWidth("260px").setFlexGrow(0);
		// kontrola na null je tady jenom proto, aby při selectu (kdy se udělá
		// nový objekt a dá se mu akorát ID, které se porovnává) aplikace
		// nespadla na NPE -- což je trochu zvláštní, protože ve skutečnosti
		// žádný majetek nemá stav null.
		Column<HWItemOverviewTO> stateColumn = grid
				.addColumn(hw -> hw.getState() == null ? "" : hw.getState().getName()).setHeader("Stav")
				.setKey(STATE_BIND).setWidth("130px").setFlexGrow(0);
		Column<HWItemOverviewTO> usedInColumn = grid.addColumn(HWItemOverviewTO::getUsedInName).setKey(USED_IN_BIND)
				.setHeader("Je součástí");
		Column<HWItemOverviewTO> supervizedColumn = grid.addColumn(HWItemOverviewTO::getSupervizedFor)
				.setKey(SUPERVIZED_FOR_BIND).setHeader("Spravováno pro");
		grid.addColumn(hw -> FieldUtils.formatMoney(hw.getPrice())).setHeader("Cena").setKey(PRICE_BIND)
				.setTextAlign(ColumnTextAlign.END);
		grid.addColumn(new LocalDateRenderer<HWItemOverviewTO>(HWItemOverviewTO::getPurchaseDate, "d.M.yyyy"))
				.setHeader("Získáno").setKey(PURCHASE_DATE_BIND).setTextAlign(ColumnTextAlign.END);

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

		grid.addItemClickListener(event -> {
			if (event.getClickCount() > 1) {
				openDetailWindow(event.getItem().getId());
				grid.select(event.getItem());
			}
		});

		add(grid);

		ButtonLayout buttonLayout = new ButtonLayout();
		add(buttonLayout);

		// Založení nové položky HW
		Button newHWBtn = new CreateButton("Přidat", e -> openItemWindow(null));
		buttonLayout.add(newHWBtn);

		// Kopie položky HW
		Button copyHWBtn = new GridButton<HWItemOverviewTO>("Zkopírovat",
				set -> copyItemWindow(set.iterator().next().getId()), grid);
		copyHWBtn.setIcon(new Image(ImageIcon.PLUS_16_ICON.createResource(), "image"));
		buttonLayout.add(copyHWBtn);

		// Zobrazení detailů položky HW
		Button detailsBtn = new GridButton<HWItemOverviewTO>("Detail",
				set -> openDetailWindow(set.iterator().next().getId()), grid);
		detailsBtn.setIcon(new Image(ImageIcon.CLIPBOARD_16_ICON.createResource(), "image"));
		buttonLayout.add(detailsBtn);

		// Oprava údajů existující položky HW
		Button fixBtn = new GridButton<HWItemOverviewTO>("Upravit", set -> openItemWindow(set.iterator().next()), grid);
		fixBtn.setIcon(new Image(ImageIcon.QUICKEDIT_16_ICON.createResource(), "image"));
		buttonLayout.add(fixBtn);

		// Smazání položky HW
		Button deleteBtn = new DeleteGridButton<HWItemOverviewTO>("Smazat", set -> {
			HWItemOverviewTO item = set.iterator().next();
			try {
				getHWService().deleteHWItem(item.getId());
				populate();
			} catch (Exception ex) {
				new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
			}
		}, grid);
		buttonLayout.add(deleteBtn);
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	private void populate() {
		filterTO.setTypes(hwTypesFilter.getValues());
		FetchCallback<HWItemOverviewTO, HWItemOverviewTO> fetchCallback = q -> getHWService().getHWItems(filterTO,
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
		CountCallback<HWItemOverviewTO, HWItemOverviewTO> countCallback = q -> getHWService().countHWItems(filterTO);
		grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
	}

	private void openItemWindow(HWItemOverviewTO hwItemOverviewTO) {
		HWItemTO hwItem = null;
		if (hwItemOverviewTO != null)
			hwItem = getHWService().getHWItem(hwItemOverviewTO.getId());
		new HWItemDialog(hwItem == null ? null : hwItem.getId()) {
			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess(HWItemTO dto) {
				populate();
				HWItemOverviewTO filterTO = new HWItemOverviewTO();
				filterTO.setId(dto.getId());
				grid.select(filterTO);
				if (hwItemOverviewTO == null)
					openDetailWindow(dto.getId());
			}
		}.open();
	}

	private void copyItemWindow(Long id) {
		Long newId = getHWService().copyHWItem(id);
		populate();
		HWItemOverviewTO newTO = new HWItemOverviewTO();
		newTO.setId(newId);
		grid.select(newTO);
		openDetailWindow(newId);
	}

	private void openDetailWindow(Long id) {
		new HWItemDetailsDialog(id) {
			private static final long serialVersionUID = 1621156205987235037L;

			public HWItemTO refreshItem() {
				HWItemTO to = super.refreshItem();
				grid.getSelectedItems().forEach(item -> {
					if (item.getId().equals(id)) {
						item.setName(to.getName());
						item.setState(to.getState());
						item.setUsedInName(to.getUsedInName());
						item.setSupervizedFor(to.getSupervizedFor());
						item.setPrice(to.getPrice());
						item.setPurchaseDate(to.getPurchaseDate());
					}
				});
				return to;
			};
		}.open();
	}

}
