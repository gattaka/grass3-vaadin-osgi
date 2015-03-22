package cz.gattserver.grass3.template;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public abstract class ModifyTableButton<T> extends TableSelectedItemBtn<T> {

	private static final long serialVersionUID = -5924239277930098183L;

	public ModifyTableButton(String caption, final AbstractSelect table) {
		super(caption, table);
		setIcon(new ThemeResource("img/tags/pencil_16.png"));
	}

	@Override
	protected ClickListener getClickListener(final AbstractSelect table) {
		return new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				Window win = getModifyWindow(getSelectedValue(table));
				UI.getCurrent().addWindow(win);
			}
		};
	}

	protected abstract Window getModifyWindow(T selectedValue);
}
