package cz.gattserver.grass3.articles.plugins.basic.list;

import org.springframework.stereotype.Component;

/**
 * 
 * @author Hynek
 *
 */
@Component
public class UnorderedListPlugin extends AbstractListPlugin {

	public UnorderedListPlugin() {
		super("UL", "articles/basic/img/ul_16.png");
	}

}
