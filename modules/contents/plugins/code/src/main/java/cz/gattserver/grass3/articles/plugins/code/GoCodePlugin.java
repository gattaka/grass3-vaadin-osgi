package cz.gattserver.grass3.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class GoCodePlugin extends AbstractCodePlugin {

	public GoCodePlugin() {
		super("GO", "Go", "go.jpg", "go", "go");
	}

}
