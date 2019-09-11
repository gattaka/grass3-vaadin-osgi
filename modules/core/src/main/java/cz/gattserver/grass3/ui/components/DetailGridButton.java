package cz.gattserver.grass3.ui.components;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;

import cz.gattserver.web.common.ui.ImageIcon;

public class DetailGridButton<T> extends GridButton<T> {

	private static final long serialVersionUID = -5924239277930098183L;

	public interface ClickListener<T> {
		public void buttonClick(T item);
	}

	public DetailGridButton(String caption, ClickListener<T> clickListener, Grid<T> grid) {
		super(caption, items -> clickListener.buttonClick(items.iterator().next()), grid);
		setIcon(new Image(ImageIcon.INFO_16_ICON.createResource(), "Info"));
		setEnableResolver(items -> items.size() == 1);
	}

}
