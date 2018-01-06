package cz.gattserver.grass3.hw.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.fo0.advancedtokenfield.main.AdvancedTokenField;
import com.fo0.advancedtokenfield.main.Token;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.LocalDateRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.hw.interfaces.HWFilterDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemState;
import cz.gattserver.grass3.hw.interfaces.HWItemTypeDTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteDTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.hw.ui.windows.HWItemCreateWindow;
import cz.gattserver.grass3.hw.ui.windows.HWItemDetailWindow;
import cz.gattserver.grass3.hw.ui.windows.ServiceNoteCreateWindow;
import cz.gattserver.grass3.model.util.QuerydslUtil;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.FieldUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmWindow;
import cz.gattserver.web.common.ui.window.ErrorWindow;

public class HWItemsTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	@Autowired
	private HWService hwFacade;

	private final String PRICE_BIND = "customPrice";
	private final String STATE_BIND = "customState";
	private final String PURCHASE_DATE_BIND = "customPurchaseDate";

	private Grid<HWItemOverviewDTO> grid;
	private AdvancedTokenField hwTypesFilter;

	private HWFilterDTO filterDTO;

	private void populate() {
		List<Token> collection = hwTypesFilter.getTokens();
		List<String> types = new ArrayList<>();
		collection.forEach(t -> {
			types.add(t.getValue());
		});
		filterDTO.setTypes(types);
		grid.setDataProvider((sortOrder, offset, limit) -> hwFacade.getHWItems(filterDTO,
				new PageRequest(offset / limit, limit), QuerydslUtil.transformOrdering(sortOrder, column -> {
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
				})).stream(), () -> hwFacade.countHWItems(filterDTO));
	}

	private void addWindow(Window win) {
		UI.getCurrent().addWindow(win);
	}

	private void openNewItemWindow(boolean fix) {
		HWItemDTO hwItem = null;
		if (fix) {
			if (grid.getSelectedItems().isEmpty())
				return;
			Long id = grid.getSelectedItems().iterator().next().getId();
			hwItem = hwFacade.getHWItem(id);
		}
		addWindow(new HWItemCreateWindow(hwItem == null ? null : hwItem.getId()) {

			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess() {
				populate();
			}
		});
	}

	private void openAddNoteWindow() {
		if (grid.getSelectedItems().isEmpty())
			return;
		Long id = grid.getSelectedItems().iterator().next().getId();
		HWItemDTO hwItem = hwFacade.getHWItem(id);

		addWindow(new ServiceNoteCreateWindow(HWItemsTab.this, hwItem) {

			private static final long serialVersionUID = -5582822648042555576L;

			@Override
			protected void onSuccess(ServiceNoteDTO noteDTO) {
				populate(); // změna stavu
			}
		});

	}

	private void openDetailWindow(Long id) {
		addWindow(new HWItemDetailWindow(id).withRefreshGrid(this::populate));
	}

	private void openDeleteWindow() {
		if (grid.getSelectedItems().isEmpty())
			return;
		HWItemsTab.this.setEnabled(false);
		HWItemOverviewDTO to = grid.getSelectedItems().iterator().next();
		addWindow(new ConfirmWindow(
				"Opravdu smazat '" + to.getName() + "' (budou smazány i servisní záznamy a údaje u součástí) ?", e -> {
					try {
						hwFacade.deleteHWItem(to.getId());
						populate();
					} catch (Exception ex) {
						UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
					}
				}) {

			private static final long serialVersionUID = -422763987707688597L;

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
		setMargin(new MarginInfo(true, false, false, false));

		final Button newHWBtn = new Button("Založit novou položku HW");
		final Button newNoteBtn = new Button("Přidat servisní záznam");
		final Button detailsBtn = new Button("Detail");
		final Button fixBtn = new Button("Upravit");
		final Button deleteBtn = new Button("Smazat");
		newNoteBtn.setEnabled(false);
		detailsBtn.setEnabled(false);
		fixBtn.setEnabled(false);
		deleteBtn.setEnabled(false);
		newHWBtn.setIcon(ImageIcon.PLUS_16_ICON.createResource());
		newNoteBtn.setIcon(ImageIcon.PENCIL_16_ICON.createResource());
		detailsBtn.setIcon(ImageIcon.CLIPBOARD_16_ICON.createResource());
		fixBtn.setIcon(ImageIcon.QUICKEDIT_16_ICON.createResource());
		deleteBtn.setIcon(ImageIcon.DELETE_16_ICON.createResource());

		// Filtr na typy HW
		hwTypesFilter = new AdvancedTokenField();
		hwTypesFilter.getInputField().setWidth("200px");
		hwTypesFilter.getInputField().addValueChangeListener(e -> {
			if (e.getValue() != null)
				hwTypesFilter.addToken(e.getValue());
		});
		hwTypesFilter.addTokenAddListener(token -> populate());
		hwTypesFilter.addTokenRemoveListener(e -> populate());
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

		// Tabulka HW
		grid = new Grid<>(HWItemOverviewDTO.class);
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setWidth("100%");

		grid.getColumn("name").setCaption("Název").setWidth(260);
		grid.getColumn("purchaseDate").setCaption("Získáno");
		grid.getColumn("usedIn").setCaption("Je součástí").setMaximumWidth(180);
		grid.getColumn("supervizedFor").setCaption("Spravováno pro");
		grid.addColumn(hw -> FieldUtils.formatMoney(hw.getPrice()), new TextRenderer()).setCaption("Cena")
				.setId(PRICE_BIND).setStyleGenerator(item -> "v-align-right");
		grid.addColumn(hw -> {
			return hw.getState().getName();
		}, new TextRenderer()).setCaption("Stav").setId(STATE_BIND).setWidth(130);
		grid.addColumn(HWItemOverviewDTO::getPurchaseDate, new LocalDateRenderer("dd.MM.yyyy")).setCaption("Získáno")
				.setId(PURCHASE_DATE_BIND).setStyleGenerator(item -> "v-align-right");

		grid.setColumns("name", STATE_BIND, "usedIn", "supervizedFor", PRICE_BIND, PURCHASE_DATE_BIND);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterDTO.setName(e.getValue());
			populate();
		});
		filteringHeader.getCell("name").setComponent(nazevColumnField);

		// Stav
		ComboBox<HWItemState> stavColumnCombo = new ComboBox<>(null, Arrays.asList(HWItemState.values()));
		stavColumnCombo.addStyleName(ValoTheme.COMBOBOX_TINY);
		stavColumnCombo.setWidth("100%");
		stavColumnCombo.addValueChangeListener(e -> {
			filterDTO.setState(e.getValue());
			populate();
		});
		filteringHeader.getCell(STATE_BIND).setComponent(stavColumnCombo);

		// Je součástí
		TextField usedInColumnField = new TextField();
		usedInColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		usedInColumnField.setWidth("100%");
		usedInColumnField.addValueChangeListener(e -> {
			filterDTO.setUsedIn(e.getValue());
			populate();
		});
		filteringHeader.getCell("usedIn").setComponent(usedInColumnField);

		// Spravován pro
		TextField supervizedForColumnField = new TextField();
		supervizedForColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		supervizedForColumnField.setWidth("100%");
		supervizedForColumnField.addValueChangeListener(e -> {
			filterDTO.setSupervizedFor(e.getValue());
			populate();
		});
		filteringHeader.getCell("supervizedFor").setComponent(supervizedForColumnField);

		populate();
		grid.sort("name");

		grid.addItemClickListener(event -> {
			if (event.getMouseEventDetails().isDoubleClick()) {
				openDetailWindow(event.getItem().getId());
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

		// Založení nové položky HW
		newHWBtn.addClickListener(e -> openNewItemWindow(false));
		buttonLayout.addComponent(newHWBtn);

		// Založení nového servisního záznamu
		newNoteBtn.addClickListener(e -> openAddNoteWindow());
		buttonLayout.addComponent(newNoteBtn);

		// Zobrazení detailů položky HW
		detailsBtn.addClickListener(e -> openDetailWindow(grid.getSelectedItems().iterator().next().getId()));
		buttonLayout.addComponent(detailsBtn);

		// Oprava údajů existující položky HW
		fixBtn.addClickListener(e -> openNewItemWindow(true));
		buttonLayout.addComponent(fixBtn);

		// Smazání položky HW
		deleteBtn.addClickListener(e -> openDeleteWindow());
		buttonLayout.addComponent(deleteBtn);

	}
}
