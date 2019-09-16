package cz.gattserver.grass3;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import cz.gattserver.web.common.spring.SpringContextHelper;

/**
 * Základní layout pro stránky systému Grass. Volá {@link SpringContextHelper}
 * pro injektování závislostí. Poskytuje metody pro vyhazování chyb na stránce,
 * přidávání JS a CSS zdrojů a získávání URL informací.
 * 
 * @author Hynek
 *
 */
@Route("unreachable")
@JsModule("./custom.js")
@Theme(value = Lumo.class)
@CssImport("./styles.css")
public class TriggerPage extends Div {
	private static final long serialVersionUID = 916372790957064247L;
}
