package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.CategoryPage;
import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.pages.template.IGrassPage;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

@Component("categoryPageFactory")
public class CategoryPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = -3658456878731957677L;

	public CategoryPageFactory() {
		super("category");
	}
	
	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new CategoryPage(request);
	}

}
