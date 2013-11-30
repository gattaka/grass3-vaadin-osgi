package org.myftp.gattserver.grass3.template;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public abstract class DetailBtn<T> extends TableSelectedItemBtn<T> {

	private static final long serialVersionUID = -5924239277930098183L;

	public DetailBtn(String caption, final Table table,
			Component... triggerComponents) {
		super(caption, table, triggerComponents);
		setIcon(new ThemeResource("img/tags/info_16.png"));
	}

	@Override
	protected ClickListener getClickListener(final Table table,
			final Component... triggerComponents) {
		return new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				Window win = getDetailWindow(getSelectedValue(table),
						triggerComponents);
				UI.getCurrent().addWindow(win);
			}
		};
	}

	protected abstract Window getDetailWindow(T selectedValue,
			Component... triggerComponents);
}