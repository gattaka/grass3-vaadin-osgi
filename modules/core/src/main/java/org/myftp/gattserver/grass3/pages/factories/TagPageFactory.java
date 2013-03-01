package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

@Component(value = "tagPageFactory")
public class TagPageFactory extends PageFactory {

	public TagPageFactory() {
		super("tag", "tagPage");
	}

}
