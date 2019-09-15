package cz.gattserver.grass3.ui.test;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;

@Route(value = "probe")
public class TestPage extends Div {

	public TestPage() {
	}

	private TreeGrid<String> createTreeGrid() {
		return null;
	}

}
