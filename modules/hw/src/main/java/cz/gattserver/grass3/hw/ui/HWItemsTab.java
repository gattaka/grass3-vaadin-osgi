package cz.gattserver.grass3.hw.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateRenderer;

import cz.gattserver.grass3.hw.interfaces.HWFilterTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass3.hw.interfaces.HWItemState;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.hw.ui.windows.HWItemDetailDialog;
import cz.gattserver.grass3.hw.ui.windows.HWItemDialog;
import cz.gattserver.grass3.hw.ui.windows.ServiceNoteCreateDialog;
import cz.gattserver.grass3.model.util.QuerydslUtil;
import cz.gattserver.grass3.ui.components.button.CreateButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.GridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.TokenField;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.FieldUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
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
		hwTypesFilter.getInputField().setWidth("200px");
		hwTypesFilter.addTokenAddListener(token -> populate());
		hwTypesFilter.addTokenRemoveListener(e -> populate());
		HorizontalLayout hwTypesFilterLayout = new HorizontalLayout();
		hwTypesFilterLayout.setSpacing(true);
		add(hwTypesFilterLayout);

		hwTypesFilterLayout.add(hwTypesFilter);

		hwTypesFilter.setAllowNewItems(false);
		hwTypesFilter.getInputField().setPlaceholder("Filtrovat dle typu hw");
		hwTypesFilter.isEnabled();

		// Tabulka HW
		grid = new Grid<>();
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setWidth("100%");

		Column<HWItemOverviewTO> nameColumn = grid.addColumn(HWItemOverviewTO::getName).setKey(NAME_BIND)
				.setHeader("Název").setWidth("260px").setFlexGrow(0);
		Column<HWItemOverviewTO> stateColumn = grid.addColumn(hw -> hw.getState().getName()).setHeader("Stav")
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
		TextField nazevColumnField = new TextField();
		nazevColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			populate();
		});
		filteringHeader.getCell(nameColumn).setComponent(nazevColumnField);

		// Stav
		ComboBox<HWItemState> stavColumnCombo = new ComboBox<>(null, Arrays.asList(HWItemState.values()));
		stavColumnCombo.getElement().setAttribute("theme", TextFieldVariant.LUMO_SMALL.name());
		stavColumnCombo.setWidth("100%");
		stavColumnCombo.addValueChangeListener(e -> {
			filterTO.setState(e.getValue());
			populate();
		});
		filteringHeader.getCell(stateColumn).setComponent(stavColumnCombo);

		// Je součástí
		TextField usedInColumnField = new TextField();
		usedInColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		usedInColumnField.setWidth("100%");
		usedInColumnField.addValueChangeListener(e -> {
			filterTO.setUsedIn(e.getValue());
			populate();
		});
		filteringHeader.getCell(usedInColumn).setComponent(usedInColumnField);

		// Spravován pro
		TextField supervizedForColumnField = new TextField();
		supervizedForColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		supervizedForColumnField.setWidth("100%");
		supervizedForColumnField.addValueChangeListener(e -> {
			filterTO.setSupervizedFor(e.getValue());
			populate();
		});
		filteringHeader.getCell(supervizedColumn).setComponent(supervizedForColumnField);

		populate();
		grid.sort(Arrays.asList(new GridSortOrder<>(nameColumn, SortDirection.ASCENDING)));

		grid.addItemClickListener(event -> {
			if (event.getClickCount() > 1)
				openDetailWindow(event.getItem().getId());
		});

		add(grid);

		ButtonLayout buttonLayout = new ButtonLayout();
		add(buttonLayout);

		// Založení nové položky HW
		Button newHWBtn = new CreateButton("Založit novou položku HW", e -> openItemWindow(null));
		buttonLayout.add(newHWBtn);

		// Založení nového servisního záznamu
		Button newNoteBtn = new ModifyGridButton<HWItemOverviewTO>("Přidat servisní záznam",
				to -> openAddNoteWindow(to), grid);
		buttonLayout.add(newNoteBtn);

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
		Button deleteBtn = new DeleteGridButton<HWItemOverviewTO>("Smazat",
				set -> openDeleteWindow(set.iterator().next()), grid);
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

	private void openItemWindow(HWItemOverviewTO to) {
		HWItemTO hwItem = null;
		if (to != null)
			hwItem = getHWService().getHWItem(to.getId());
		new HWItemDialog(hwItem == null ? null : hwItem.getId()) {
			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess(HWItemTO dto) {
				populate();
				HWItemOverviewTO filterTO = new HWItemOverviewTO();
				filterTO.setId(dto.getId());
				grid.select(filterTO);
			}
		}.open();
	}

	private void openAddNoteWindow(HWItemOverviewTO to) {
		HWItemTO hwItem = getHWService().getHWItem(to.getId());
		new ServiceNoteCreateDialog(hwItem) {
			private static final long serialVersionUID = -5582822648042555576L;

			@Override
			protected void onSuccess(ServiceNoteTO noteDTO) {
				populate();
			}
		}.open();
	}

	private void openDetailWindow(Long id) {
		new HWItemDetailDialog(id).setChangeListener(this::populate).open();
	}

	private void openDeleteWindow(HWItemOverviewTO to) {
		new ConfirmDialog(
				"Opravdu smazat '" + to.getName() + "' (budou smazány i servisní záznamy a údaje u součástí) ?", e -> {
					try {
						getHWService().deleteHWItem(to.getId());
						populate();
					} catch (Exception ex) {
						new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
					}
				}) {

			private static final long serialVersionUID = -422763987707688597L;

			@Override
			public void close() {
				HWItemsTab.this.setEnabled(true);
				super.close();
			}

		}.open();
	}

}
