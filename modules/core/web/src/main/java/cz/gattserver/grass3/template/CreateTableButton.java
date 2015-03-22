package cz.gattserver.grass3.template;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public abstract class CreateTableButton extends Button {

	private static final long serialVersionUID = -5924239277930098183L;

	public CreateTableButton(String caption) {
		setIcon(new ThemeResource("img/tags/plus_16.png"));
		setCaption(caption);
		addClickListener(getClickListener());
	}

	protected ClickListener getClickListener() {
		return new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				Window win = getCreateWindow();
				UI.getCurrent().addWindow(win);
			}
		};
	}

	protected abstract Window getCreateWindow();
}
