package cz.gattserver.grass3.components;

import java.util.function.Function;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

public class GridButton<T> extends Button {

	private static final long serialVersionUID = -5924239277930098183L;

	private Grid<T> grid;
	private Function<T, Boolean> enableResolver;

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
		enableResolver = t -> !grid.getSelectedItems().isEmpty();
		grid.addSelectionListener(
				e -> GridButton.this.setEnabled(enableResolver.apply(e.getFirstSelectedItem().get())));
	}

	public GridButton<T> setEnableResolver(Function<T, Boolean> enableResolver) {
		this.enableResolver = enableResolver;
		return this;
	}

}
