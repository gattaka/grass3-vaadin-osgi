package cz.gattserver.grass3.medic.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("medicPageFactory")
public class MedicPageFactory extends AbstractPageFactory {

	public MedicPageFactory() {
		super("medic");
	}

}
