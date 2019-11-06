package cz.gattserver.grass3.pg.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("pgEditorPageFactory")
public class PGEditorPageFactory extends AbstractPageFactory {

	public PGEditorPageFactory() {
		super("pg-editor");
	}

}
