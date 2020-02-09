package cz.gattserver.grass3.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class BASHCodePlugin extends AbstractCodePlugin {

	public BASHCodePlugin() {
		super("BASH", "BASH", "bash.gif", "shell", "shell");
	}

}
