package cz.gattserver.grass3.campgames.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("campgamesPageFactory")
public class CampgamesPageFactory extends AbstractPageFactory {

	public CampgamesPageFactory() {
		super("campgames");
	}

}
