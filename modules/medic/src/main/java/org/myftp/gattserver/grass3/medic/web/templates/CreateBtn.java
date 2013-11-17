package org.myftp.gattserver.grass3.medic.web.templates;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public abstract class CreateBtn extends GrassBtn {

	private static final long serialVersionUID = -5924239277930098183L;

	public CreateBtn(String caption, Component... triggerComponents) {
		super(caption, triggerComponents);
		setIcon(new ThemeResource("img/tags/plus_16.png"));
	}

	@Override
	protected ClickListener getClickListener(final Component... triggerComponents) {
		return new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				Window win = getCreateWindow(
						triggerComponents);
				UI.getCurrent().addWindow(win);
			}
		};
	}

	protected abstract Window getCreateWindow(Component... triggerComponents);
}
