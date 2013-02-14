package sandbox;

import sandbox.interfaces.IPageFactory;

import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

public class EditorPage extends BasePage {

	private static final long serialVersionUID = 502625699429764791L;

	public static enum EditorPageFactory implements IPageFactory {

		INSTANCE;

		@Override
		public String getPageName() {
			return "editor";
		}

		@Override
		public Component createPage() {
			return new EditorPage();
		}
	}

	@Override
	protected void createContent(CustomLayout layout) {
		CustomLayout contentLayout = new CustomLayout("oneColumn");
		layout.addComponent(contentLayout, "content");

		Button button = new Button("View", new Button.ClickListener() {
			private static final long serialVersionUID = 7646166365866861567L;

			@Override
			public void buttonClick(ClickEvent event) {
				Page.getCurrent().setLocation(
						ViewPage.ViewPageFactory.INSTANCE.getPageName());
			}
		});
		contentLayout.addComponent(button, "content");

	}

}
