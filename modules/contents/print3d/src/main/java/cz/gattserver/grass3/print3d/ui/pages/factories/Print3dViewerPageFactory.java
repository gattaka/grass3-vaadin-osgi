package cz.gattserver.grass3.print3d.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("print3dViewerPageFactory")
public class Print3dViewerPageFactory extends AbstractPageFactory {

	public Print3dViewerPageFactory() {
		super("print3d");
	}
}
