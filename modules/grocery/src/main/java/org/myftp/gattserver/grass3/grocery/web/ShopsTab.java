package org.myftp.gattserver.grass3.grocery.web;

import java.util.Collection;

import org.myftp.gattserver.grass3.grocery.dto.ShopDTO;
import org.myftp.gattserver.grass3.ui.util.GrassFilterDecorator;
import org.tepi.filtertable.FilterTable;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

public class ShopsTab extends GroceryPageTab<ShopDTO> {

	private static final long serialVersionUID = -5013459007975657195L;

	public ShopsTab() {
		super(ShopDTO.class);
	}

	@Override
	protected Collection<ShopDTO> getTableItems() {
		return groceryFacade.getAllShops();
	}

	@Override
	protected Window createCreateWindow(Component... triggerComponent) {
		return new ShopCreateWindow(triggerComponent) {
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
	protected Window createModifyWindow(ShopDTO dto, Component... triggerComponent) {
		return new ShopCreateWindow(dto, triggerComponent) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected void deleteEntity(ShopDTO dto) {
		groceryFacade.deleteShop(dto);
	}

	@Override
	protected AbstractSelect createTable() {

		FilterTable table = new FilterTable();
		table.setContainerDataSource(createContainer());

		table.setColumnHeader("name", "Název");
		table.setWidth("100%");
		table.setSelectable(true);
		table.setImmediate(true);
		table.setVisibleColumns(new Object[] { "name" });

		table.setFilterBarVisible(true);
		table.setFilterDecorator(new GrassFilterDecorator());

		return table;
	}

}
