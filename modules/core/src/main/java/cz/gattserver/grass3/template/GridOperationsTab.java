package cz.gattserver.grass3.template;

import java.util.Collection;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.gattserver.common.Identifiable;

/**
 * Template všech tabů, kde je v tabulce zobrazován přehled entit a dole jsou
 * tlačítka operací Vytvořit/Detail/Upravit/Smazat
 * 
 * @author Hynek
 * @param <T>
 *            třída zobrazované entity
 * 
 */
public abstract class GridOperationsTab<T extends Identifiable> extends VerticalLayout {

	private static final long serialVersionUID = 6844434642906509277L;

	protected Grid<T> grid;
	protected Collection<T> data;

	/**
	 * Vytvoří okno pro založení entity
	 */
	protected abstract Window createCreateWindow();

	/**
	 * Vytvoří okno pro detail entity
	 */
	protected abstract Window createDetailWindow(Long id);

	/**
	 * Vytvoří okno pro úpravu entity
	 */
	protected abstract Window createModifyWindow(T dto);

	/**
	 * Smaže vybranou entitu
	 */
	protected abstract void deleteEntity(T dto);

	/**
	 * Upraví tabulku (jmenuje sloupce apod.) - voláno pouze pokud je použit
	 * defaultní Grid - viz metoda createGrid
	 */
	protected void customizeGrid(Grid<T> grid) {
	}

	protected abstract Collection<T> getItems();

	public GridOperationsTab(Class<T> clazz) {
		setSpacing(true);
		setMargin(true);

		init();

		/**
		 * Přehled
		 */
		grid = new Grid<T>(clazz);
		grid.setItems(getItems());
		customizeGrid(grid);
		addComponent(grid);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		final Button createBtn = new CreateGridButton("Založit", e -> UI.getCurrent().addWindow(createCreateWindow()));

		final Button detailBtn = new DetailGridButton<T>("Detail",
				(e, item) -> UI.getCurrent().addWindow(createDetailWindow(item.getId())), grid);

		final Button modifyBtn = new ModifyGridButton<T>("Upravit",
				(e, item) -> UI.getCurrent().addWindow(createModifyWindow(item)), grid);

		final Button deleteBtn = new DeleteGridButton<T>("Smazat", item -> {
			deleteEntity(item);
			data.remove(item);
			grid.getDataProvider().refreshAll();
		}, grid);

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

}
