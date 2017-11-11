package cz.gattserver.grass3.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

public class GridButton<T> extends Button {

	private static final long serialVersionUID = -5924239277930098183L;

	private Grid<T> grid;

	public interface ClickListener<T> {
		public void buttonClick(ClickEvent event, T item);
	}

	public GridButton(String caption, Grid<T> grid) {
		this(caption, null, grid);
	}

	public void setClickListener(ClickListener<T> clickListener) {
		super.addClickListener(e -> clickListener.buttonClick(e, grid.getSelectedItems().iterator().next()));
	}

	public GridButton(String caption, ClickListener<T> clickListener, Grid<T> grid) {
		super(caption);
		this.grid = grid;
		setEnabled(false);
		if (clickListener != null)
			setClickListener(clickListener);
		grid.addSelectionListener(e -> GridButton.this.setEnabled(e.getAllSelectedItems().size() == 1));
	}

}
