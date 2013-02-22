package sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sandbox.EditorPage.EditorPageFactory;
import sandbox.HomePage.HomePageFactory;
import sandbox.ViewPage.ViewPageFactory;
import sandbox.interfaces.IPageFactory;
import sandbox.util.GrassRequest;
import sandbox.util.PageFactoriesMap;
import sandbox.util.URLPathAnalyzer;

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
	private static Logger logger = LoggerFactory.getLogger(MyVaadinUI.class);

	PageFactoriesMap factoriesMap = new PageFactoriesMap(
			HomePageFactory.INSTANCE);

	public MyVaadinUI() {
		factoriesMap.put(EditorPageFactory.INSTANCE);
		factoriesMap.put(ViewPageFactory.INSTANCE);
	}

	@Override
	protected void init(VaadinRequest request) {

		String path = request.getPathInfo();
		logger.info("Path: [" + path + "]");

		GrassRequest grassRequest = new GrassRequest(request);
		URLPathAnalyzer analyzer = grassRequest.getAnalyzer();

		IPageFactory factory = factoriesMap.get(analyzer.getPathToken(0));
		setContent(factory.createPage(grassRequest));

	}
}
