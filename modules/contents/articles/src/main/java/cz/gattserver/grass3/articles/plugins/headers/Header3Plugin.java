package cz.gattserver.grass3.articles.plugins.headers;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.plugins.headers.AbstractHeaderPlugin;

/**
 * @author gatt
 */
@Component
public class Header3Plugin extends AbstractHeaderPlugin {

	public Header3Plugin() {
		super(3);
	}
}
