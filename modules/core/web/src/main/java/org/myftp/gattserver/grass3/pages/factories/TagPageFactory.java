package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component(value = "tagPageFactory")
public class TagPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = -5921551750646968643L;

	public TagPageFactory() {
		super("tag", "tagPage");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}
}
