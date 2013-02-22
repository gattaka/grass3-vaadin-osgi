package sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sandbox.interfaces.IPageFactory;
import sandbox.util.GrassRequest;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;

public class EditorPage extends BasePage {

	private static final long serialVersionUID = 502625699429764791L;
	private static Logger logger = LoggerFactory.getLogger(EditorPage.class);

	public static enum EditorPageFactory implements IPageFactory {

		INSTANCE;

		@Override
		public String getPageName() {
			return "editor";
		}

		@Override
		public Component createPage(GrassRequest request) {
			return new EditorPage(request);
		}
	}

	public EditorPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void createContent(CustomLayout layout) {
		CustomLayout contentLayout = new CustomLayout("oneColumn");
		layout.addComponent(contentLayout, "content");

		String article = getRequest().getAnalyzer().getPathToken(1);
		logger.info("Článek: [" + article + "]");

		contentLayout.addComponent(new Label(article), "content");

	}
}
