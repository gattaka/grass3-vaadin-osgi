package org.myftp.gattserver.grass3.grocery.web;

import java.util.Collection;
import java.util.Locale;

import org.myftp.gattserver.grass3.grocery.dto.PurchaseDTO;
import org.myftp.gattserver.grass3.template.TableSelectedItemBtn;
import org.myftp.gattserver.grass3.ui.util.StringToDateConverter;
import org.myftp.gattserver.grass3.ui.util.StringToFixedSizeDoubleConverter;
import org.myftp.gattserver.grass3.ui.util.StringToMoneyConverter;
import org.tepi.filtertable.FilterDecorator;
import org.tepi.filtertable.FilterTable;
import org.tepi.filtertable.numberfilter.NumberFilterPopupConfig;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomTable.Align;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class PurchaseTab extends GroceryPageTab<PurchaseDTO> {

	private static final long serialVersionUID = -5013459007975657195L;

	public PurchaseTab() {
		super(PurchaseDTO.class);
	}

	@Override
	protected Collection<PurchaseDTO> getTableItems() {
		return groceryFacade.getAllPurchases();
	}

	@Override
	protected Window createCreateWindow(Component... triggerComponent) {
		return new PurchaseCreateWindow(triggerComponent) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected void placeButtons(HorizontalLayout buttonLayout, Button createBtn, Button detailBtn, Button modifyBtn,
			Button deleteBtn) {
		buttonLayout.addComponent(createBtn);
		buttonLayout.addComponent(new TableSelectedItemBtn<PurchaseDTO>("Pokračovat", table) {
			private static final long serialVersionUID = 5668486295786220721L;

			@Override
			protected ClickListener getClickListener(final AbstractSelect table, final Component... triggerComponents) {
				setIcon(new ThemeResource("img/tags/plus_16.png")); // side-effect
				return new Button.ClickListener() {
					private static final long serialVersionUID = 4070242729318498324L;

					@Override
					public void buttonClick(ClickEvent event) {
						Window win = new PurchaseCreateWindow(PurchaseCreateWindow.Mode.CONTINUE, (PurchaseDTO) table
								.getValue(), triggerComponents) {
							private static final long serialVersionUID = -7566950396535469316L;

							@Override
							protected void onSuccess() {
								populateContainer();
							}
						};
						UI.getCurrent().addWindow(win);
					}
				};
			};
		});
		buttonLayout.addComponent(modifyBtn);
		buttonLayout.addComponent(deleteBtn);
	}

	@Override
	protected Window createDetailWindow(Long id, Component... triggerComponent) {
		return null;
	}

	@Override
	protected Window createModifyWindow(PurchaseDTO dto, Component... triggerComponent) {
		return new PurchaseCreateWindow(PurchaseCreateWindow.Mode.EDIT, dto, triggerComponent) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected void deleteEntity(PurchaseDTO dto) {
		groceryFacade.deletePurchase(dto);
	}

	@Override
	protected AbstractSelect createTable() {
		FilterTable table = new FilterTable();

		table.setContainerDataSource(createContainer());

		table.setColumnHeader("date", "Datum");
		table.setColumnHeader("shop", "Obchod");
		table.setColumnHeader("product", "Produkt");
		table.setColumnHeader("cost", "Cena za kus");
		table.setColumnHeader("quantity", "Množství");
		table.setColumnHeader("costSum", "Cena");
		table.setConverter("date", new StringToDateConverter("d.M.yyyy"));
		table.setConverter("cost", new StringToMoneyConverter());
		table.setConverter("costSum", new StringToMoneyConverter());
		table.setConverter("quantity", new StringToFixedSizeDoubleConverter(0, 3));
		table.setColumnAlignment("cost", Align.RIGHT);
		table.setColumnAlignment("quantity", Align.RIGHT);
		table.setColumnAlignment("costSum", Align.RIGHT);
		table.setSortContainerPropertyId("costSum");

		table.setWidth("100%");
		table.setSelectable(true);
		table.setImmediate(true);
		table.setVisibleColumns(new Object[] { "date", "shop", "product", "cost", "quantity", "costSum" });

		table.setFilterBarVisible(true);
		table.setFilterDecorator(new FilterDecorator() {
			private static final long serialVersionUID = 234978256739631357L;

			@Override
			public boolean usePopupForNumericProperty(Object propertyId) {
				return false;
			}

			@Override
			public boolean isTextFilterImmediate(Object propertyId) {
				return true;
			}

			@Override
			public String getToCaption() {
				return "Do";
			}

			@Override
			public int getTextChangeTimeout(Object propertyId) {
				return 0;
			}

			@Override
			public String getSetCaption() {
				return "Filtrovat";
			}

			@Override
			public NumberFilterPopupConfig getNumberFilterPopupConfig() {
				return null;
			}

			@Override
			public Locale getLocale() {
				return new Locale("cs");
			}

			@Override
			public String getFromCaption() {
				return "Od";
			}

			@Override
			public Resource getEnumFilterIcon(Object propertyId, Object value) {
				return null;
			}

			@Override
			public String getEnumFilterDisplayName(Object propertyId, Object value) {
				return null;
			}

			@Override
			public String getDateFormatPattern(Object propertyId) {
				return "d.M.yyyy";
			}

			@Override
			public Resolution getDateFieldResolution(Object propertyId) {
				return Resolution.DAY;
			}

			@Override
			public String getClearCaption() {
				return "Vymazat";
			}

			@Override
			public Resource getBooleanFilterIcon(Object propertyId, boolean value) {
				return null;
			}

			@Override
			public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
				return null;
			}

			@Override
			public String getAllItemsVisibleString() {
				return null;
			}
		});

		return table;
	}
}