package cz.gattserver.grass3.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component(value = "tagPageFactory")
public class TagPageFactory extends AbstractPageFactory {

	public TagPageFactory() {
		super("tag");
	}

}
