package cz.gattserver.grass3.articles.plugins.basic.list;

import org.springframework.stereotype.Component;

/**
 * 
 * @author Hynek
 *
 */
@Component
public class OrderedListPlugin extends AbstractListPlugin {

	public OrderedListPlugin() {
		super("OL", "articles/basic/img/ol_16.png");
	}

}
