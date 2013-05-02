package sandbox;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;

//@JavaScript({ "http://localhost:8080/VAADIN/themes/grass/js/jquery.js",
//		"http://localhost:8080/VAADIN/themes/grass/js/jquery-ui.js" })
@JavaScript({ "js/jquery.js", "js/jquery-ui.js" })
public class JQueryComponent extends AbstractJavaScriptComponent {

	private static final long serialVersionUID = -4447432773048101436L;

	public JQueryComponent() {
	}

}
