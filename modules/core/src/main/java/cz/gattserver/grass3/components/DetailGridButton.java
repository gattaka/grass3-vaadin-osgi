package cz.gattserver.grass3.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Grid;

import cz.gattserver.web.common.ui.ImageIcons;

public class DetailGridButton<T> extends GridButton<T> {

	private static final long serialVersionUID = -5924239277930098183L;

	public DetailGridButton(String caption, GridButton.ClickListener<T> clickListener, Grid<T> grid) {
		super(caption, clickListener, grid);
		setIcon(new ThemeResource(ImageIcons.INFO_16_ICON));
	}

}
