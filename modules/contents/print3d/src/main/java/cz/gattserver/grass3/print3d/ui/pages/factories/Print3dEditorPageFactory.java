package cz.gattserver.grass3.print3d.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("print3dEditorPageFactory")
public class Print3dEditorPageFactory extends AbstractPageFactory {

	public Print3dEditorPageFactory() {
		super("print3d-editor");
	}

}
