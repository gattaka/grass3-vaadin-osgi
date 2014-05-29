package org.myftp.gattserver.grass3.template;

import java.util.Collection;

import org.myftp.gattserver.grass3.model.dto.Identifiable;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Template všech tabů, kde je v tabulce zobrazován přehled entit a dole jsou
 * tlačítka operací Vytvořit/Detail/Upravit/Smazat
 * 
 * @author Hynek
 * @param <T>
 *            třída zobrazované entity
 * 
 */
public abstract class TableOperationsTab<T extends Identifiable> extends VerticalLayout implements ISelectable {

	private static final long serialVersionUID = 6844434642906509277L;

	protected AbstractSelect table;
	protected Container container;
	protected Class<T> clazz;

	/**
	 * Získá entity pro tabulku
	 */
	protected abstract Collection<T> getTableItems();

	/**
	 * Vytvoří okno pro založení entity
	 */
	protected abstract Window createCreateWindow(Component... triggerComponent);

	/**
	 * Vytvoří okno pro detail entity
	 */
	protected abstract Window createDetailWindow(Long id, Component... triggerComponent);

	/**
	 * Vytvoří okno pro úpravu entity
	 */
	protected abstract Window createModifyWindow(T dto, Component... triggerComponent);

	/**
	 * Smaže vybranou entitu
	 */
	protected abstract void deleteEntity(T dto);

	/**
	 * Upraví tabulku (jmenuje sloupce apod.) - voláno pouze pokud je použit
	 * defaultní Table - viz metoda createTable
	 */
	protected void customizeTable(Table table) {
	}

	protected Container createContainer() {
		BeanItemContainer<T> container = new BeanItemContainer<>(clazz);
		container.addAll(getTableItems());
		this.container = container;
		return container;
	}

	protected AbstractSelect createTable() {
		Table table = new Table();
		table.setContainerDataSource(createContainer());
		customizeTable(table);
		return table;
	}

	public TableOperationsTab(Class<T> clazz) {
		
		this.clazz = clazz;

		setSpacing(true);
		setMargin(true);

		init();

		/**
		 * Přehled
		 */
		addComponent(table = createTable());

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		final Button createBtn = new CreateTableButton("Založit") {
			private static final long serialVersionUID = -6624403751552838924L;

			@Override
			protected Window getCreateWindow(Component... triggerComponents) {
				return createCreateWindow(triggerComponents);
			}
		};

		final Button detailBtn = new DetailTableButton<T>("Detail", table, TableOperationsTab.this) {
			private static final long serialVersionUID = -8949928545479455240L;

			@Override
			protected Window getDetailWindow(T selectedValue, Component... triggerComponent) {
				return createDetailWindow(selectedValue.getId(), triggerComponent);
			}
		};

		final Button modifyBtn = new ModifyTableButton<T>("Upravit", table, TableOperationsTab.this) {
			private static final long serialVersionUID = -8949928545479455240L;

			@Override
			protected Window getModifyWindow(T selectedValue, Component... triggerComponent) {
				return createModifyWindow(selectedValue, triggerComponent);
			}
		};

		final Button deleteBtn = new DeleteTableButton<T>("Smazat", table, TableOperationsTab.this) {

			private static final long serialVersionUID = 1900185891293966049L;

			@Override
			protected void onConfirm(T selectedValue) {
				deleteEntity(selectedValue);
				populateContainer();
			}
		};

		placeButtons(buttonLayout, createBtn, detailBtn, modifyBtn, deleteBtn);

	}

	protected void placeButtons(HorizontalLayout buttonLayout, Button createBtn, Button detailBtn, Button modifyBtn,
			Button deleteBtn) {
		buttonLayout.addComponent(createBtn);
		buttonLayout.addComponent(detailBtn);
		buttonLayout.addComponent(modifyBtn);
		buttonLayout.addComponent(deleteBtn);
	}

	protected void init() {
	};

	@Override
	public void setEnabled(boolean enabled) {
		// BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
		super.setEnabled(enabled);
		table.setEnabled(enabled);
	}

	/**
	 * Naplní tabulku položkami
	 */
	public void populateContainer() {
		table.setContainerDataSource(createContainer());
	}

	@Override
	public void select() {
		// tady nic není potřeba
	}

}
