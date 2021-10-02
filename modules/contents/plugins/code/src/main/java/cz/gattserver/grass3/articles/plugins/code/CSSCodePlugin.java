package cz.gattserver.grass3.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class CSSCodePlugin extends AbstractCodePlugin {

	public CSSCodePlugin() {
		super("CSS", "CSS", "", "css", "text/css");
	}

}
