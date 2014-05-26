package org.myftp.gattserver.grass3;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

public class GrassUIProvider extends UIProvider {

	private static final long serialVersionUID = 4156516778293500959L;

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return GrassUI.class;
	}

}
