package org.myftp.gattserver.grass3.articles.windows;

import org.myftp.gattserver.grass3.windows.template.TwoColumnWindow;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ArticlesEditorWindow extends TwoColumnWindow {

	private static final long serialVersionUID = -5148523174527532785L;

	@Override
	protected void createLeftColumnContent(VerticalLayout layout) {
	
		layout.addComponent(new Label("Levé menu editoru"));
		
	}

	@Override
	protected void createRightColumnContent(VerticalLayout layout) {

		layout.addComponent(new Label("Pravé menu editoru"));
		
	}

}
