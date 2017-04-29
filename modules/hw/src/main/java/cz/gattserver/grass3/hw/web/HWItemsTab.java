package cz.gattserver.grass3.hw.web;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterGenerator;
import org.tepi.filtertable.FilterTable;
import org.tepi.filtertable.datefilter.DateInterval;
import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.tokenfield.TokenField;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomTable.Align;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.gattserver.grass3.hw.dto.HWFilterDTO;
import cz.gattserver.grass3.hw.dto.HWItemDTO;
import cz.gattserver.grass3.hw.dto.HWItemState;
import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.dto.ServiceNoteDTO;
import cz.gattserver.grass3.hw.facade.IHWFacade;
import cz.gattserver.grass3.ui.util.GrassFilterDecorator;
import cz.gattserver.grass3.ui.util.StringToDateConverter;
import cz.gattserver.grass3.ui.util.StringToMoneyConverter;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.ErrorWindow;

public class HWItemsTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	@Autowired
	private IHWFacade hwFacade;

	private final FilterTable table = new FilterTable();
	private LazyQueryContainer container;
	private TokenField hwTypesFilter;

	private HWFilterDTO filterDTO;

	// BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		table.setEnabled(enabled);
	}

	private void populateContainer() {
		@SuppressWarnings("unchecked")
		Collection<String> collection = (Collection<String>) hwTypesFilter.getValue();
		filterDTO.setTypes(collection);
		container.refresh();
	}

	private void sortTable() {
		table.sort(new Object[] { "name" }, new boolean[] { true });
	}

	private void addWindow(Window win) {
		UI.getCurrent().addWindow(win);
	}

	private void openNewItemWindow(boolean fix) {
		HWItemDTO hwItem = null;
		if (fix) {
			Long id = (Long) table.getValue();
			hwItem = hwFacade.getHWItem(id);
		}
		addWindow(new HWItemCreateWindow(HWItemsTab.this, hwItem == null ? null : hwItem.getId()) {

			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		});
	}

	private void openAddNoteWindow() {
		Long id = (Long) table.getValue();
		HWItemDTO hwItem = hwFacade.getHWItem(id);

		addWindow(new ServiceNoteCreateWindow(HWItemsTab.this, hwItem) {

			private static final long serialVersionUID = -5582822648042555576L;

			@Override
			protected void onSuccess(ServiceNoteDTO noteDTO) {
				populateContainer(); // změna stavu
			}
		});

	}

	private void openDetailWindow() {
		Long id = (Long) table.getValue();
		if (id == null)
			return;
		addWindow(new HWItemDetailWindow(HWItemsTab.this, id));
	}

	private void openDeleteWindow() {
		HWItemsTab.this.setEnabled(false);
		Long id = (Long) table.getValue();
		HWItemDTO hwItem = hwFacade.getHWItem(id);
		addWindow(new ConfirmWindow(
				"Opravdu smazat '" + hwItem.getName() + "' (budou smazány i servisní záznamy a údaje u součástí) ?") {

			private static final long serialVersionUID = -422763987707688597L;

			@Override
			protected void onConfirm(ClickEvent event) {
				if (hwFacade.deleteHWItem(hwItem)) {
					populateContainer();
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
		newHWBtn.setIcon(new ThemeResource("img/tags/plus_16.png"));
		newNoteBtn.setIcon(new ThemeResource("img/tags/pencil_16.png"));
		detailsBtn.setIcon(new ThemeResource("img/tags/clipboard_16.png"));
		fixBtn.setIcon(new ThemeResource("img/tags/quickedit_16.png"));
		deleteBtn.setIcon(new ThemeResource("img/tags/delete_16.png"));

		/**
		 * Filtr na typy HW
		 */
		// menu tagů + textfield tagů
		// http://marc.virtuallypreinstalled.com/TokenField/
		hwTypesFilter = new TokenField();
		HorizontalLayout hwTypesFilterLayout = new HorizontalLayout();
		hwTypesFilterLayout.setSpacing(true);
		addComponent(hwTypesFilterLayout);

		hwTypesFilterLayout.addComponent(hwTypesFilter);

		Set<HWItemTypeDTO> hwTypes = hwFacade.getAllHWTypes();
		BeanContainer<String, HWItemTypeDTO> tokens = new BeanContainer<String, HWItemTypeDTO>(HWItemTypeDTO.class);
		tokens.setBeanIdProperty("name");
		tokens.addAll(hwTypes);

		hwTypesFilter.setStyleName(TokenField.STYLE_TOKENFIELD);
		hwTypesFilter.setContainerDataSource(tokens);
		hwTypesFilter.setNewTokensAllowed(false);
		hwTypesFilter.setFilteringMode(FilteringMode.CONTAINS); // suggest
		hwTypesFilter.setTokenCaptionPropertyId("name");
		hwTypesFilter.setInputPrompt("Filtrovat dle typu hw");
		hwTypesFilter.isEnabled();

		hwTypesFilter.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -3648782288654270789L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				populateContainer();
			}
		});

		/**
		 * Tabulka HW
		 */
		table.setSelectable(true);
		table.setImmediate(true);
		BeanQueryFactory<HWQuery> factory = new BeanQueryFactory<HWQuery>(HWQuery.class);
		Map<String, Object> conf = new HashMap<>();
		conf.put(HWQuery.FILTER_KEY, filterDTO);
		factory.setQueryConfiguration(conf);
		container = new LazyQueryContainer(factory, "id", 100, false);
		container.addContainerProperty("name", String.class, "");
		container.addContainerProperty("purchaseDate", Date.class, null);
		container.addContainerProperty("price", BigDecimal.class, null);
		container.addContainerProperty("state", HWItemState.class, null);
		container.addContainerProperty("usedInName", String.class, "");

		table.setContainerDataSource(container);

		table.setConverter("purchaseDate", new StringToDateConverter());
		table.setConverter("price", new StringToMoneyConverter());

		table.setColumnHeader("name", "Název");
		table.setColumnHeader("purchaseDate", "Získáno");
		table.setColumnHeader("price", "Cena");
		table.setColumnHeader("state", "Stav");
		table.setColumnHeader("usedInName", "Je součástí");

		table.setColumnAlignment("price", Align.RIGHT);

		table.setVisibleColumns(new Object[] { "name", "state", "usedInName", "price", "purchaseDate" });
		table.setColumnWidth("name", 380);
		table.setColumnWidth("usedInName", 180);
		table.setWidth("100%");

		table.setFilterBarVisible(true);
		table.setFilterDecorator(new GrassFilterDecorator() {
			private static final long serialVersionUID = -6862621820503893204L;

			@Override
			public boolean isTextFilterImmediate(Object propertyId) {
				return false;
			}
		});
		table.setFilterGenerator(new FilterGenerator() {
			private static final long serialVersionUID = 8801368960933927218L;

			@Override
			public AbstractField<?> getCustomFilterComponent(Object propertyId) {
				return null;
			}

			private void updateFilter(Object propertyId, Object value) {
				switch ((String) propertyId) {
				case "name":
					filterDTO.setName((String) value);
					break;
				case "purchaseDate":
					filterDTO.setPurchaseDateFrom(value == null ? null : ((DateInterval) value).getFrom());
					filterDTO.setPurchaseDateTo(value == null ? null : ((DateInterval) value).getTo());
					break;
				case "price":
					filterDTO.setPrice((BigDecimal) value);
					break;
				case "state":
					filterDTO.setState((HWItemState) value);
					break;
				case "usedInName":
					filterDTO.setUsedIn((String) value);
					break;
				}
				container.refresh();
			}

			@Override
			public Filter generateFilter(Object propertyId, Field<?> originatingField) {
				return null;
			}

			@Override
			public Filter generateFilter(Object propertyId, Object value) {
				return null;
			}

			@Override
			public void filterRemoved(Object propertyId) {
				updateFilter(propertyId, null);
			}

			@Override
			public Filter filterGeneratorFailed(Exception reason, Object propertyId, Object value) {
				return null;
			}

			@Override
			public void filterAdded(Object propertyId, Class<? extends Filter> filterType, Object value) {
				updateFilter(propertyId, value);
			}
		});

		sortTable();

		table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			private static final long serialVersionUID = 2068314108919135281L;

			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					openDetailWindow();
				}
			}
		});

		table.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean enabled = table.getValue() != null;
				deleteBtn.setEnabled(enabled);
				detailsBtn.setEnabled(enabled);
				newNoteBtn.setEnabled(enabled);
				fixBtn.setEnabled(enabled);
			}
		});

		addComponent(table);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Založení nové položky HW
		 */
		newHWBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openNewItemWindow(false);
			}
		});
		buttonLayout.addComponent(newHWBtn);

		/**
		 * Založení nového servisního záznamu
		 */
		newNoteBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 8876001665427003203L;

			public void buttonClick(ClickEvent event) {
				openAddNoteWindow();
			}

		});
		buttonLayout.addComponent(newNoteBtn);

		/**
		 * Zobrazení detailů položky HW
		 */
		detailsBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				openDetailWindow();
			}
		});
		buttonLayout.addComponent(detailsBtn);

		/**
		 * Oprava údajů existující položky HW
		 */
		fixBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openNewItemWindow(true);
			}
		});
		buttonLayout.addComponent(fixBtn);

		/**
		 * Smazání položky HW
		 */
		deleteBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				openDeleteWindow();
			}
		});
		buttonLayout.addComponent(deleteBtn);

	}
}
