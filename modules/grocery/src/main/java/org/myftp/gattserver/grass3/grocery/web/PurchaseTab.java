package org.myftp.gattserver.grass3.grocery.web;

import java.util.Collection;

import org.myftp.gattserver.grass3.grocery.dto.PurchaseDTO;
import org.myftp.gattserver.grass3.ui.util.StringToDateConverter;
import org.myftp.gattserver.grass3.ui.util.StringToFixedSizeDoubleConverter;
import org.myftp.gattserver.grass3.ui.util.StringToMoneyConverter;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Table.Align;

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
		buttonLayout.addComponent(modifyBtn);
		buttonLayout.addComponent(deleteBtn);
	}

	@Override
	protected Window createDetailWindow(Long id, Component... triggerComponent) {
		return null;
	}

	@Override
	protected Window createModifyWindow(PurchaseDTO dto, Component... triggerComponent) {
		return new PurchaseCreateWindow(dto, triggerComponent) {
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
	protected void customizeTable(Table table) {

		table.addGeneratedColumn("costSum", new Table.ColumnGenerator() {
			private static final long serialVersionUID = -1984983998391825571L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				@SuppressWarnings("unchecked")
				PurchaseDTO purchaseDTO = ((BeanItem<PurchaseDTO>) source.getItem(itemId)).getBean();
				return new StringToMoneyConverter().format(purchaseDTO.getCost() * purchaseDTO.getQuantity());
			}
		});

		table.setColumnHeader("date", "Datum");
		table.setColumnHeader("shop", "Obchod");
		table.setColumnHeader("product", "Produkt");
		table.setColumnHeader("cost", "Cena za kus");
		table.setColumnHeader("quantity", "Množství");
		table.setColumnHeader("costSum", "Cena");
		table.setConverter("date", new StringToDateConverter("d.M.yyyy"));
		table.setConverter("cost", new StringToMoneyConverter());
		table.setConverter("quantity", new StringToFixedSizeDoubleConverter());
		table.setColumnAlignment("cost", Align.RIGHT);
		table.setColumnAlignment("quantity", Align.RIGHT);
		table.setColumnAlignment("costSum", Align.RIGHT);

		table.setWidth("100%");
		table.setSelectable(true);
		table.setImmediate(true);
		table.setVisibleColumns(new Object[] { "date", "shop", "product", "cost", "quantity", "costSum" });
	}
}
