package cz.gattserver.grass3.ui.pages.settings;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.components.NodeTree;
import cz.gattserver.web.common.ui.H2Label;

public class NodesSettingsPage extends AbstractSettingsPage {

	public NodesSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		NodeTree tree = new NodeTree(true);
		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout usersLayout = new VerticalLayout();
		layout.addComponent(usersLayout);

		usersLayout.addComponent(new H2Label("Správa kategorií"));
		usersLayout.addComponent(tree);
		return layout;
	}

}
