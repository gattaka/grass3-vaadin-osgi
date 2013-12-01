package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component(value = "noServicePageFactory")
public class NoServicePageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = -8393895002312736988L;

	public NoServicePageFactory() {
		super("noservice", "noServicePage");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}
}
