package cz.gattserver.grass3.template;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import cz.gattserver.web.common.ui.ImageIcons;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.ErrorWindow;

public abstract class DeleteTableButton<T> extends TableSelectedItemBtn<T> {

	private static final long serialVersionUID = -5924239277930098183L;

	public DeleteTableButton(String caption, final AbstractSelect table) {
		super(caption, table);
		setIcon(new ThemeResource(ImageIcons.DELETE_16_ICON));
	}

	@Override
	protected ClickListener getClickListener(final AbstractSelect table) {
		return new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				final T selectedValue = getSelectedValue(table);
				Window win = new ConfirmWindow(getConfirmMessage(selectedValue)) {

					private static final long serialVersionUID = -422763987707688597L;

					@Override
					protected void onConfirm(ClickEvent event) {
						try {
							DeleteTableButton.this.onConfirm(selectedValue);
						} catch (Exception e) {
							onError();
						}
					}
				};
				UI.getCurrent().addWindow(win);
			}
		};
	}

	protected void onError() {
		UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
	}

	protected String getConfirmMessage(T item) {
		return "Opravdu si přejete smazat vybranou položku ?";
	}

	protected abstract void onConfirm(T selectedValue);
}
