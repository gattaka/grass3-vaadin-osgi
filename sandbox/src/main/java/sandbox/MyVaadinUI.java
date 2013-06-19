package sandbox;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

/**
 * The Application's "main" class
 */
@Title("Gattserver")
@Theme("grass")
@Scope("prototype")
@Component("grassUI")
public class MyVaadinUI extends UI {

	private static final long serialVersionUID = -7296936167498820319L;

	@Override
	protected void init(VaadinRequest request) {

		HorizontalLayout layout = new HorizontalLayout();
		setContent(layout);

		layout.setWidth("100%"); 
		
		MenuBar menuBar = new MenuBar();
		menuBar.setWidth("100%");
		layout.addComponent(menuBar);
		layout.setComponentAlignment(menuBar, Alignment.MIDDLE_LEFT);
		layout.setExpandRatio(menuBar, 1);

		menuBar.addItem("Domů", new Command() {
			private static final long serialVersionUID = -4748802744382335974L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				Notification.show("Domů");
			}
		});

		menuBar.addItem("Hardware", new Command() {
			private static final long serialVersionUID = -4748802744382335974L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				Notification.show("Hardware");
			}
		});

		MenuItem aplikaceItem = menuBar.addItem("Aplikace", null);
		
		aplikaceItem.addItem("Vyhledávání", new Command() {
			private static final long serialVersionUID = -4748802744382335974L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				Notification.show("Vyhledávání");
			}
		});
		
		MenuBar userBar = new MenuBar();
//		userBar.setWidth("100%");
		layout.addComponent(userBar);
		layout.setComponentAlignment(userBar, Alignment.MIDDLE_RIGHT);
		
		userBar.addItem("gatt", new Command() {
			private static final long serialVersionUID = -4748802744382335974L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				Notification.show("Gatt");
			}
		});

	}
}
