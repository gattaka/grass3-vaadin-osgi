package cz.gattserver.grass3.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class CCodePlugin extends AbstractCodePlugin {

	public CCodePlugin() {
		super("C", "C", "", "clike", "text/x-csrc");
	}
}
