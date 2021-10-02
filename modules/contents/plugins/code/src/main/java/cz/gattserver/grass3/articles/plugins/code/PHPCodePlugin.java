package cz.gattserver.grass3.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class PHPCodePlugin extends AbstractCodePlugin {

	public PHPCodePlugin() {
		super("PHP", "PHP", "php.png", "php", "text/x-php");
	}

}
