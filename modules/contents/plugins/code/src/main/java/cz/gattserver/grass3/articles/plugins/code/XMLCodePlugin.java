package cz.gattserver.grass3.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class XMLCodePlugin extends AbstractCodePlugin {

	public XMLCodePlugin() {
		super("XML", "HTML/XML", "", "xml", "xml");
	}

}
