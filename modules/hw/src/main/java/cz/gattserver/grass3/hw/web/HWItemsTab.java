package cz.gattserver.grass3.hw.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.fo0.advancedtokenfield.main.Token;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.LocalDateRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import cz.gattserver.grass3.hw.dto.HWFilterDTO;
import cz.gattserver.grass3.hw.dto.HWItemDTO;
import cz.gattserver.grass3.hw.dto.HWItemOverviewDTO;
import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.dto.ServiceNoteDTO;
import cz.gattserver.grass3.hw.facade.HWFacade;
import cz.gattserver.grass3.model.util.QuerydslUtil;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.ui.FieldUtils;
import cz.gattserver.web.common.ui.ImageIcons;
import cz.gattserver.web.common.ui.TokenField;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.ErrorWindow;

public class HWItemsTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	@Autowired
	private HWFacade hwFacade;

	private final String PRICE_BIND = "customPrice";
	private final String STATE_BIND = "customState";
	private final String PURCHASE_DATE_BIND = "customPurchaseDate";

	private Grid<HWItemOverviewDTO> grid;
	private TokenField hwTypesFilter;

	private HWFilterDTO filterDTO;

	// BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		grid.setEnabled(enabled);
	}

	private void populate() {
		List<Token> collection = hwTypesFilter.getTokens();
		List<String> types = new ArrayList<>();
		collection.forEach(t -> {
			types.add(t.getValue());
		});
		filterDTO.setTypes(types);
		grid.setDataProvider((sortOrder, offset, limit) -> {
			return hwFacade.getHWItems(filterDTO, new PageRequest(offset / limit, limit),
					QuerydslUtil.transformOrdering(sortOrder, column -> {
						switch (column) {
						case PRICE_BIND:
							return "price";
						case STATE_BIND:
							return "state";
						case PURCHASE_DATE_BIND:
							return "purchaseDate";
						default:
							return column;
						}
					})).stream();
		}, () -> {
			return hwFacade.countHWItems(filterDTO);
		});
	}

	private void addWindow(Window win) {
		UI.getCurrent().addWindow(win);
	}

	private void openNewItemWindow(boolean fix) {
		HWItemDTO hwItem = null;
		if (fix) {
			// TODO
			Long id = null; // (Long) table.getValue();
			hwItem = hwFacade.getHWItem(id);
		}
		addWindow(new HWItemCreateWindow(HWItemsTab.this, hwItem == null ? null : hwItem.getId()) {

			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess() {
				populate();
			}
		});
	}

	private void openAddNoteWindow() {
		// TODO
		Long id = null; // (Long) table.getValue();
		HWItemDTO hwItem = hwFacade.getHWItem(id);

		addWindow(new ServiceNoteCreateWindow(HWItemsTab.this, hwItem) {

			private static final long serialVersionUID = -5582822648042555576L;

			@Override
			protected void onSuccess(ServiceNoteDTO noteDTO) {
				populate(); // změna stavu
			}
		});

	}

	private void openDetailWindow() {
		if (grid.getSelectedItems().isEmpty())
			return;
		addWindow(new HWItemDetailWindow(HWItemsTab.this, grid.getSelectedItems().iterator().next().getId()) {
			private static final long serialVersionUID = -8711057204738112594L;

			@Override
			protected void refreshTable() {
				populate();
			}
		});
	}

	private void openDeleteWindow() {
		if (grid.getSelectedItems().isEmpty())
			return;
		HWItemsTab.this.setEnabled(false);
		HWItemOverviewDTO to = grid.getSelectedItems().iterator().next();
		addWindow(new ConfirmWindow(
				"Opravdu smazat '" + to.getName() + "' (budou smazány i servisní záznamy a údaje u součástí) ?") {

			private static final long serialVersionUID = -422763987707688597L;

			@Override
			protected void onConfirm(ClickEvent event) {
				if (hwFacade.deleteHWItem(to.getId())) {
					populate();
				} else {
					UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
				}
			}

			@Override
			public void close() {
				HWItemsTab.this.setEnabled(true);
				super.close();
			}
		});
	}

	public HWItemsTab() {
		SpringContextHelper.inject(this);

		filterDTO = new HWFilterDTO();

		setSpacing(true);
		setMargin(true);

		// final Button filterByTypeBtn = new Button("Filtrovat dle typu");
		final Button newHWBtn = new Button("Založit novou položku HW");
		final Button newNoteBtn = new Button("Přidat servisní záznam");
		final Button detailsBtn = new Button("Detail");
		final Button fixBtn = new Button("Upravit");
		final Button deleteBtn = new Button("Smazat");
		newNoteBtn.setEnabled(false);
		detailsBtn.setEnabled(false);
		fixBtn.setEnabled(false);
		deleteBtn.setEnabled(false);
		newHWBtn.setIcon(new ThemeResource(ImageIcons.PLUS_16_ICON));
		newNoteBtn.setIcon(new ThemeResource(ImageIcons.PENCIL_16_ICON));
		detailsBtn.setIcon(new ThemeResource(ImageIcons.CLIPBOARD_16_ICON));
		fixBtn.setIcon(new ThemeResource(ImageIcons.QUICKEDIT_16_ICON));
		deleteBtn.setIcon(new ThemeResource(ImageIcons.DELETE_16_ICON));

		/**
		 * Filtr na typy HW
		 */
		hwTypesFilter = new TokenField();
		hwTypesFilter.addTokenAddListener(t -> {
			populate();
			return t;
		});
		hwTypesFilter.addTokenRemoveListener(t -> {
			populate();
			return t;
		});
		HorizontalLayout hwTypesFilterLayout = new HorizontalLayout();
		hwTypesFilterLayout.setSpacing(true);
		addComponent(hwTypesFilterLayout);

		hwTypesFilterLayout.addComponent(hwTypesFilter);

		Set<HWItemTypeDTO> hwTypes = hwFacade.getAllHWTypes();
		hwTypes.forEach(t -> {
			Token to = new Token(t.getName());
			hwTypesFilter.addTokenToInputField(to);
		});
		hwTypesFilter.setAllowNewItems(false);
		hwTypesFilter.getInputField().setPlaceholder("Filtrovat dle typu hw");
		hwTypesFilter.isEnabled();

		/**
		 * Tabulka HW
		 */
		grid = new Grid<>(HWItemOverviewDTO.class);
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setWidth("100%");

		grid.getColumn("name").setCaption("Název").setWidth(300);
		grid.getColumn("purchaseDate").setCaption("Získáno");
		grid.getColumn("usedIn").setCaption("Je součástí").setWidth(180);
		grid.getColumn("supervizedFor").setCaption("Spravováno pro");
		grid.addColumn(hw -> {
			return FieldUtils.formatMoney(hw.getPrice());
		}, new TextRenderer()).setCaption("Cena").setId(PRICE_BIND).setStyleGenerator(item -> "v-align-right");
		grid.addColumn(hw -> {
			return hw.getState().getName();
		}, new TextRenderer()).setCaption("Stav").setId(STATE_BIND);
		grid.addColumn(HWItemOverviewDTO::getPurchaseDate, new LocalDateRenderer("dd.MM.yyyy")).setCaption("Získáno")
				.setId(PURCHASE_DATE_BIND).setStyleGenerator(item -> "v-align-right");

		grid.setColumns("name", STATE_BIND, "usedIn", "supervizedFor", PRICE_BIND, PURCHASE_DATE_BIND);

		populate();
		grid.sort("name");

		grid.addItemClickListener(event -> {
			if (event.getMouseEventDetails().isDoubleClick()) {
				openDetailWindow();
			}
		});

		grid.addSelectionListener(e -> {
			boolean enabled = e.getFirstSelectedItem().isPresent();
			deleteBtn.setEnabled(enabled);
			detailsBtn.setEnabled(enabled);
			newNoteBtn.setEnabled(enabled);
			fixBtn.setEnabled(enabled);
		});

		addComponent(grid);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Založení nové položky HW
		 */
		newHWBtn.addClickListener(e -> openNewItemWindow(false));
		buttonLayout.addComponent(newHWBtn);

		/**
		 * Založení nového servisního záznamu
		 */
		newNoteBtn.addClickListener(e -> openAddNoteWindow());
		buttonLayout.addComponent(newNoteBtn);

		/**
		 * Zobrazení detailů položky HW
		 */
		detailsBtn.addClickListener(e -> openDetailWindow());
		buttonLayout.addComponent(detailsBtn);

		/**
		 * Oprava údajů existující položky HW
		 */
		fixBtn.addClickListener(e -> openNewItemWindow(true));
		buttonLayout.addComponent(fixBtn);

		/**
		 * Smazání položky HW
		 */
		deleteBtn.addClickListener(e -> openDeleteWindow());
		buttonLayout.addComponent(deleteBtn);

	}
}
