package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

@Component("categoryPageFactory")
public class CategoryPageFactory extends PageFactory {

	public CategoryPageFactory() {
		super("category", "categoryPage");
	}

}
