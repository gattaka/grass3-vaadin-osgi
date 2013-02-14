package sandbox;

import sandbox.MyVaadinUI.Views;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

public class EditorPage extends VerticalLayout implements View {

	private static final long serialVersionUID = 502625699429764791L;

	public EditorPage(final Navigator navigator) {
		setSizeFull();

		addComponent(new Label("Editor"));

		Button button = new Button("View",
				new Button.ClickListener() {
					private static final long serialVersionUID = 7646166365866861567L;

					@Override
					public void buttonClick(ClickEvent event) {
						navigator.navigateTo(Views.VIEW.name());
					}
				});
		addComponent(button);
		setComponentAlignment(button, Alignment.MIDDLE_CENTER);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
