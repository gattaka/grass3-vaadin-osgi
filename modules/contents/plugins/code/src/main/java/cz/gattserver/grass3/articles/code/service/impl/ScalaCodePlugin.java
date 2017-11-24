package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class ScalaCodePlugin extends AbstractCodePlugin {

	public ScalaCodePlugin() {
		super("SCALA", "Scala", "scala.gif", "clike", "scala");
	}

}
