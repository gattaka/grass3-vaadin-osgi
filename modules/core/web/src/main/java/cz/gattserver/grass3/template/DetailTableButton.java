package cz.gattserver.grass3.template;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import cz.gattserver.web.common.ui.ImageIcons;

public abstract class DetailTableButton<T> extends TableSelectedItemBtn<T> {

	private static final long serialVersionUID = -5924239277930098183L;

	public DetailTableButton(String caption, final AbstractSelect table) {
		super(caption, table);
		setIcon(new ThemeResource(ImageIcons.INFO_16_ICON));
	}

	@Override
	protected ClickListener getClickListener(final AbstractSelect table) {
		return new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				Window win = getDetailWindow(getSelectedValue(table));
				UI.getCurrent().addWindow(win);
			}
		};
	}

	protected abstract Window getDetailWindow(T selectedValue);
}
