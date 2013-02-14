package sandbox;

import java.util.HashMap;
import java.util.Map;

import sandbox.interfaces.IPageFactory;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 * The Application's "main" class
 */
@Title("Gattserver")
@Theme("grass")
public class MyVaadinUI extends UI {

	private static final long serialVersionUID = -7296936167498820319L;

	Map<String, IPageFactory> map = new HashMap<String, IPageFactory>();

	public MyVaadinUI() {
		map.put(EditorPage.EditorPageFactory.INSTANCE.getPageName(),
				EditorPage.EditorPageFactory.INSTANCE);
		map.put(ViewPage.ViewPageFactory.INSTANCE.getPageName(),
				ViewPage.ViewPageFactory.INSTANCE);
	}

	@Override
	protected void init(VaadinRequest request) {

		String path = request.getPathInfo();
		System.out.println("Path: " + path);

		IPageFactory factory;
		if (path.equals("/") || (factory = map.get(path.substring(1))) == null)
			setContent(ViewPage.ViewPageFactory.INSTANCE.createPage());
		else {
			setContent(factory.createPage());
		}

	}
}
