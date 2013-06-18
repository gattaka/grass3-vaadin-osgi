package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component(value = "tagPageFactory")
public class TagPageFactory extends AbstractPageFactory {

	public TagPageFactory() {
		super("tag", "tagPage");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}
}
