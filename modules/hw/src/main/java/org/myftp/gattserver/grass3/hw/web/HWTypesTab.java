package org.myftp.gattserver.grass3.hw.web;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class HWTypesTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	public HWTypesTab() {
		
		setSpacing(true);
		setMargin(true);

		addComponent(new Label("Jsem přehled typů zařízení"));
	}
}
