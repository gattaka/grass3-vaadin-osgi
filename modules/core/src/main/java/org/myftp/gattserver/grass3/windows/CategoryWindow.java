package org.myftp.gattserver.grass3.windows;

import java.util.Map;

import org.myftp.gattserver.grass3.template.Breadcrumb;
import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class CategoryWindow extends OneColumnWindow {

	private static final long serialVersionUID = -499585200973560016L;

	/**
	 * Breadcrumb
	 */
	private Breadcrumb breadcrumb;

	@Override
	protected void createContent(VerticalLayout layout) {
		layout.addComponent(breadcrumb = new Breadcrumb());
	}

	@Override
	public void handleParameters(Map<String, String[]> parameters) {

//		layout.removeAllComponents();

		super.handleParameters(parameters);
	}

}
