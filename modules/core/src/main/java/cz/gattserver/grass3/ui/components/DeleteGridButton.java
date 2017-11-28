package cz.gattserver.grass3.ui.components;

import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.ErrorWindow;

public class DeleteGridButton<T> extends GridButton<T> {

	private static final long serialVersionUID = -5924239277930098183L;

	public interface ConfirmAction<T> {
		public void onConfirm(T selectedValue);
	}

	public DeleteGridButton(String caption, ConfirmAction<T> confirmAction, Grid<T> grid) {
		super(caption, grid);
		setClickListener((e, item) -> {
			Window win = new ConfirmWindow(getConfirmMessage(item), ev -> {
				try {
					confirmAction.onConfirm(item);
				} catch (Exception ex) {
					onError();
				}
			});
			UI.getCurrent().addWindow(win);
		});
		setIcon(ImageIcon.DELETE_16_ICON.createResource());
	}

	protected void onError() {
		UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
	}

	protected String getConfirmMessage(T item) {
		return "Opravdu si přejete smazat vybranou položku ?";
	}

}
