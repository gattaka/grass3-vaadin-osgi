package cz.gattserver.grass3.ui.components;

import java.util.Set;

import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmWindow;

public class DeleteGridButton<T> extends GridButton<T> {

	private static final long serialVersionUID = -5924239277930098183L;

	public interface ConfirmAction<T> {
		public void onConfirm(Set<T> items);
	}

	public DeleteGridButton(String caption, ConfirmAction<T> confirmAction, Grid<T> grid) {
		super(caption, grid);
		setClickListener(items -> {
			Window win = new ConfirmWindow(getConfirmMessage(), ev -> confirmAction.onConfirm(items));
			UI.getCurrent().addWindow(win);
		});
		setIcon(ImageIcon.DELETE_16_ICON.createResource());
	}

	protected String getConfirmMessage() {
		return "Opravdu si přejete smazat vybrané položky?";
	}

}
