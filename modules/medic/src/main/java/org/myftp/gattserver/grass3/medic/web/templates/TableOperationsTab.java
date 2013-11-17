package org.myftp.gattserver.grass3.medic.web.templates;

import java.util.Collection;

import org.myftp.gattserver.grass3.medic.dto.Identifiable;
import org.myftp.gattserver.grass3.medic.web.ISelectable;
import org.myftp.gattserver.grass3.ui.util.StringToPreviewConverter;

import com.vaadin.data.util.BeanItemContainer;
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
public abstract class TableOperationsTab<T extends Identifiable> extends
		VerticalLayout implements ISelectable {

	private static final long serialVersionUID = 6844434642906509277L;

	private final Table table = new Table();
	private BeanItemContainer<T> container;

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

	public TableOperationsTab(Class<T> clazz) {

		setSpacing(true);
		setMargin(true);

		init();

		/**
		 * Přehled
		 */
		container = new BeanItemContainer<T>(clazz);
		populateContainer();
		table.setContainerDataSource(container);

		table.setColumnHeader("name", "Název");
		table.setColumnHeader("address", "Adresa");
		table.setColumnHeader("web", "Stránky");
		table.setWidth("100%");
		table.setSelectable(true);
		table.setImmediate(true);
		table.setConverter("web", new StringToPreviewConverter(50));
		table.setVisibleColumns(new String[] { "name", "address", "web" });
		addComponent(table);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		final Button createBtn = new CreateBtn("Založit") {
			private static final long serialVersionUID = -6624403751552838924L;

			@Override
			protected Window getCreateWindow(Component... triggerComponents) {
				return createCreateWindow(triggerComponents);
			}
		};

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

		final Button deleteBtn = new DeleteBtn<T>("Smazat", table,
				TableOperationsTab.this) {

			private static final long serialVersionUID = 1900185891293966049L;

			@Override
			protected void onConfirm(T selectedValue) {
				deleteEntity(selectedValue);
				populateContainer();
			}
		};

		/**
		 * Založení nové instituce
		 */
		buttonLayout.addComponent(createBtn);

		/**
		 * Detail instituce
		 */
		buttonLayout.addComponent(detailBtn);

		/**
		 * Úprava doktora
		 */
		buttonLayout.addComponent(modifyInstitutionBtn);

		/**
		 * Smazání instituce
		 */
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
		container.removeAllItems();
		container.addAll(getTableItems());
	}

	@Override
	public void select() {
		// tady nic není potřeba
	}

}
