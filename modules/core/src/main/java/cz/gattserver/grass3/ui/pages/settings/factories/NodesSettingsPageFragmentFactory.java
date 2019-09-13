package cz.gattserver.grass3.ui.pages.settings.factories;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.grass3.ui.components.NodeTree;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;

public class NodesSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

	@Override
	public void createFragment(Div layout) {
		NodeTree tree = new NodeTree(true);

		VerticalLayout usersLayout = new VerticalLayout();
		layout.add(usersLayout);

		usersLayout.add(new H2("Správa kategorií"));
		usersLayout.add(tree);
	}

}
