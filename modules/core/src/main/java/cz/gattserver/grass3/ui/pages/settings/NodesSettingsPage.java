package cz.gattserver.grass3.ui.pages.settings;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.components.NodeTree;

public class NodesSettingsPage extends AbstractSettingsPage {

	public NodesSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		NodeTree tree = new NodeTree(true);
		VerticalLayout layout = new VerticalLayout();

		layout.setPadding(true);
		layout.setSpacing(true);

		VerticalLayout usersLayout = new VerticalLayout();
		layout.add(usersLayout);

		usersLayout.add(new H2("Správa kategorií"));
		usersLayout.add(tree);
		return layout;
	}

}
