package org.myftp.gattserver.grass3.grocery.web;

import java.util.Collection;

import org.myftp.gattserver.grass3.grocery.dto.ProductDTO;
import org.myftp.gattserver.grass3.ui.util.GrassFilterDecorator;
import org.tepi.filtertable.FilterTable;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

public class ProductTab extends GroceryPageTab<ProductDTO> {

	private static final long serialVersionUID = -5013459007975657195L;

	public ProductTab() {
		super(ProductDTO.class);
	}

	@Override
	protected Collection<ProductDTO> getTableItems() {
		return groceryFacade.getAllProducts();
	}

	@Override
	protected Window createCreateWindow(Component... triggerComponent) {
		return new ProductCreateWindow(triggerComponent) {
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
	protected Window createModifyWindow(ProductDTO dto, Component... triggerComponent) {
		return new ProductCreateWindow(dto, triggerComponent) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected void deleteEntity(ProductDTO dto) {
		groceryFacade.deleteProduct(dto);
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
