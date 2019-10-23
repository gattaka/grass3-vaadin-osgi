package cz.gattserver.grass3.ui.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import cz.gattserver.web.common.ui.Strong;

/**
 * https://divtable.com/generator/
 * 
 * @author gattaka
 *
 */
public class GridLayout extends Div {

	private static final long serialVersionUID = 8940148875365037922L;

	private Div currentRow;
	private Div currentCell;

	public GridLayout() {
		getStyle().set("display", "table");
	}

	public GridLayout newRow() {
		currentRow = new Div();
		currentRow.getStyle().set("display", "table-row");
		super.add(currentRow);
		return this;
	}

	public GridLayout addStrong(String value) {
		add(new Strong(value));
		return this;
	}

	public GridLayout addNewCell() {
		if (currentRow == null)
			newRow();
		currentCell = new Div();
		currentCell.getStyle().set("display", "table-cell").set("padding-right", "var(--lumo-space-m)")
				.set("padding-bottom", "var(--lumo-space-m)");
		currentRow.add(currentCell);
		return this;
	}

	public void add(Component... components) {
		addNewCell();
		currentCell.add(components);
	}

}
