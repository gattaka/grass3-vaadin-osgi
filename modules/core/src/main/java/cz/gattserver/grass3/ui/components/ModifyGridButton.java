package cz.gattserver.grass3.ui.components;

import com.vaadin.ui.Grid;

import cz.gattserver.web.common.ui.ImageIcon;

public class ModifyGridButton<T> extends GridButton<T> {

	private static final long serialVersionUID = -5924239277930098183L;

	public ModifyGridButton(String caption, GridButton.ClickListener<T> clickListener, Grid<T> grid) {
		super(caption, clickListener, grid);
		setIcon(ImageIcon.PENCIL_16_ICON.createResource());
	}

}
