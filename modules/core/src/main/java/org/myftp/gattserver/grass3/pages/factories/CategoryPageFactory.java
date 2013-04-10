package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("categoryPageFactory")
public class CategoryPageFactory extends AbstractPageFactory {

	public CategoryPageFactory() {
		super("category", "categoryPage");
	}

}
