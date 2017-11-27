package cz.gattserver.grass3.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.NodePage;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("nodePageFactory")
public class NodePageFactory extends AbstractPageFactory {

	public NodePageFactory() {
		super("category");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new NodePage(request);
	}

}
