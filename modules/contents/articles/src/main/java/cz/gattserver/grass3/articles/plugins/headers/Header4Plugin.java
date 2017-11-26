package cz.gattserver.grass3.articles.plugins.headers;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.plugins.headers.AbstractHeaderPlugin;

/**
 * @author gatt
 */
@Component
public class Header4Plugin extends AbstractHeaderPlugin {

    public Header4Plugin() {
        super(4);
    }
}
