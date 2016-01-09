package cz.gattserver.grass3.recipes.web.in;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.grass3.recipes.web.out.TagElement;
import cz.gattserver.grass3.recipes.web.out.WebElement;

public abstract class Component {

	protected List<String> classes;

	public Component setClass(String name) {
		if (classes == null)
			classes = new ArrayList<String>();
		classes.add(name);
		return this;
	}

	public WebElement constructWithStyles() {
		WebElement element = construct();
		if (classes != null && element instanceof TagElement) {
			TagElement tagElement = (TagElement) element;
			for (String clas : classes)
				tagElement.setClass(clas);
		}
		return element;
	}

	public abstract WebElement construct();

}
