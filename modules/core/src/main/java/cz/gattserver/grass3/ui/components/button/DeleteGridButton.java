package cz.gattserver.grass3.ui.components.button;

import java.util.Set;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;

import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmDialog;

public class DeleteGridButton<T> extends GridButton<T> {

	private static final long serialVersionUID = -5924239277930098183L;

	private static final String CONFIRM_MSG = "Opravdu si přejete smazat vybrané položky?";

	public interface ConfirmAction<T> {
		public void onConfirm(Set<T> items);
	}

	public interface ConfirmMsgFactory<T> {
		public String create(Set<T> items);
	}

	public DeleteGridButton(String caption, ConfirmAction<T> confirmAction, Grid<T> grid) {
		this(caption, confirmAction, items -> CONFIRM_MSG, grid);
	}

	public DeleteGridButton(String caption, ConfirmAction<T> confirmAction, ConfirmMsgFactory<T> confirmMsgFactory,
			Grid<T> grid) {
		super(caption, grid);
		setClickListener(
				items -> new ConfirmDialog(confirmMsgFactory.create(items), ev -> confirmAction.onConfirm(items))
						.open());
		setIcon(new Image(ImageIcon.DELETE_16_ICON.createResource(), "Smazat"));
	}

}
