package cz.gattserver.grass3.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class SQLCodePlugin extends AbstractCodePlugin {

	public SQLCodePlugin() {
		super("SQL", "SQL", "", "sql", "sql");
	}

}
