package sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sandbox.interfaces.IPageFactory;
import sandbox.util.GrassRequest;

import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Button.ClickEvent;

public class ViewPage extends BasePage {

	private static final long serialVersionUID = 502625699429764791L;
	private static Logger logger = LoggerFactory.getLogger(ViewPage.class);

	public static enum ViewPageFactory implements IPageFactory {

		INSTANCE;

		@Override
		public String getPageName() {
			return "view";
		}

		@Override
		public Component createPage(GrassRequest request) {
			return new ViewPage(request);
		}
	}

	public ViewPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void createContent(CustomLayout layout) {
		CustomLayout contentLayout = new CustomLayout("oneColumn");
		layout.addComponent(contentLayout, "content");

		String article = getRequest().getAnalyzer().getPathToken(1);
		logger.info("Článek: [" + article + "]");

		final String editorLink = "/"
				+ EditorPage.EditorPageFactory.INSTANCE.getPageName() + "/"
				+ article;

		Button button = new Button("Editor: " + article,
				new Button.ClickListener() {
					private static final long serialVersionUID = 7646166365866861567L;

					@Override
					public void buttonClick(ClickEvent event) {
						Page.getCurrent().setLocation(editorLink);
					}
				});
		contentLayout.addComponent(button, "content");

	}
}
