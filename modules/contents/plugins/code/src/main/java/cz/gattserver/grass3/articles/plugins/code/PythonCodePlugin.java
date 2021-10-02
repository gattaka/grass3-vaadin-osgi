package cz.gattserver.grass3.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class PythonCodePlugin extends AbstractCodePlugin {

	public PythonCodePlugin() {
		super("PYTHON", "Python", "python.png", "python", "text/x-python");
	}

}
