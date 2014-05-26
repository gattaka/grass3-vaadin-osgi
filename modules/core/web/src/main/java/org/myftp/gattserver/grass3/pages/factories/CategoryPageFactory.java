package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("categoryPageFactory")
public class CategoryPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = -3658456878731957677L;

	public CategoryPageFactory() {
		super("category", "categoryPage");
	}
	
	@Override
	protected boolean isAuthorized() {
		return true;
	}

}
