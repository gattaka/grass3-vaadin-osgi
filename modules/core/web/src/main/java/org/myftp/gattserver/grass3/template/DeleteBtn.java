package org.myftp.gattserver.grass3.template;

import org.myftp.gattserver.grass3.subwindows.ConfirmWindow;
import org.myftp.gattserver.grass3.subwindows.ErrorWindow;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public abstract class DeleteBtn<T> extends TableSelectedItemBtn<T> {

	private static final long serialVersionUID = -5924239277930098183L;

	public DeleteBtn(String caption, final AbstractSelect table, Component... triggerComponents) {
		super(caption, table, triggerComponents);
		setIcon(new ThemeResource("img/tags/delete_16.png"));
	}

	@Override
	protected ClickListener getClickListener(final AbstractSelect table, final Component... triggerComponents) {
		return new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				final T selectedValue = getSelectedValue(table);
				Window win = new ConfirmWindow(getConfirmMessage(selectedValue), triggerComponents) {

					private static final long serialVersionUID = -422763987707688597L;

					@Override
					protected void onConfirm(ClickEvent event) {
						try {
							DeleteBtn.this.onConfirm(selectedValue);
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
