package cz.gattserver.grass3.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class JavaCodePlugin extends AbstractCodePlugin {

	public JavaCodePlugin() {
		super("JAVA", "Java", "java.png", "clike", "text/x-java");
	}

}
