package cz.gattserver.grass3.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class CPPCodePlugin extends AbstractCodePlugin {

	public CPPCodePlugin() {
		super("CPP", "C++", "", "clike", "cpp");
	}
}
