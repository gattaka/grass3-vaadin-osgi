package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class JavaCodePlugin extends AbstractCodePlugin {

	public JavaCodePlugin() {
		super("JAVA", "Java", "java.png", "clike", "java");
	}

}
