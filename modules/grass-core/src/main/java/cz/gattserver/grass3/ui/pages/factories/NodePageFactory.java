package cz.gattserver.grass3.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("nodePageFactory")
public class NodePageFactory extends AbstractPageFactory {

	public NodePageFactory() {
		super("category");
	}

}
