package org.myftp.gattserver.grass3.medic.web.templates;

import java.util.Collection;

import org.myftp.gattserver.grass3.medic.dto.Identifiable;
import org.myftp.gattserver.grass3.medic.web.ISelectable;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
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
public abstract class TableOperationsTab<T extends Identifiable> extends
		VerticalLayout implements ISelectable {

	private final Table table = new Table();
	private BeanItemContainer<T> container;

	/**
	 * Získá entity pro tabulku
	 */
	protected abstract Collection<T> getTableItems();

	/**
	 * Vytvoří okno pro založení entity
	 */

	/**
	 * Vytvoří okno pro detail entity
	 */
	protected abstract Window createDetailWindow(Long id,
			Component... triggerComponent);

	/**
	 * Vytvoří okno pro úpravu entity
	 */
	protected abstract Window createModifyWindow(T dto,
			Component... triggerComponent);

	/**
	 * Smaže vybranou entitu
	 */
	protected abstract void deleteEntity(T dto);
	
	/**
	 * Vytvoří okno pro úpravu entity
	 */

	public TableOperationsTab() {
		setSpacing(true);
		setMargin(true);

		final Button detailBtn = new DetailBtn<T>("Detail", table,
				TableOperationsTab.this) {
			private static final long serialVersionUID = -8949928545479455240L;

			@Override
			protected Window getDetailWindow(T selectedValue,
					Component... triggerComponent) {
				return createDetailWindow(selectedValue.getId(),
						triggerComponent);
			}
		};

		final Button modifyInstitutionBtn = new ModifyBtn<T>("Upravit", table,
				TableOperationsTab.this) {
			private static final long serialVersionUID = -8949928545479455240L;

			@Override
			protected Window getModifyWindow(T selectedValue,
					Component... triggerComponent) {
				return createModifyWindow(selectedValue, triggerComponent);
			}
		};

		final Button deleteBtn = new DeleteBtn<T>("Smazat",
				table, TableOperationsTab.this) {

			private static final long serialVersionUID = 1900185891293966049L;

			@Override
			protected void onConfirm(T selectedValue) {
				deleteEntity(selectedValue);
				populateContainer();
			}
		};

	}

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
		container.removeAllItems();
		container.addAll(getTableItems());
	}

}
