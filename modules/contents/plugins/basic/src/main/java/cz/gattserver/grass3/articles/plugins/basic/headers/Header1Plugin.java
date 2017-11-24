package cz.gattserver.grass3.articles.plugins.basic.headers;

import org.springframework.stereotype.Component;

/**
 * @author gatt
 */
@Component
public class Header1Plugin extends AbstractHeaderPlugin {

    public Header1Plugin() {
        super(1);
    }
}
