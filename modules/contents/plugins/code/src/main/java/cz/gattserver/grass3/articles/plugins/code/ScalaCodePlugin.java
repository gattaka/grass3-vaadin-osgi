package cz.gattserver.grass3.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class ScalaCodePlugin extends AbstractCodePlugin {

	public ScalaCodePlugin() {
		super("SCALA", "Scala", "scala.gif", "clike", "text/x-scala");
	}

}
